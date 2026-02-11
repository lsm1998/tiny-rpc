package com.lsm1998.rpc.consumer;

import com.lsm1998.rpc.codec.RequestEncoder;
import com.lsm1998.rpc.codec.TinyDecoder;
import com.lsm1998.rpc.protocol.Request;
import com.lsm1998.rpc.protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;

public class RpcClient implements AutoCloseable {
    private final String host;
    private final int port;
    private final EventLoopGroup group;
    private Channel channel;
    private CompletableFuture<Response> pendingResponse;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    }

    public void connect() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new TinyDecoder());
                        ch.pipeline().addLast(new RequestEncoder());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<Response>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Response response) {
                                if (pendingResponse != null) {
                                    pendingResponse.complete(response);
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                if (pendingResponse != null) {
                                    pendingResponse.completeExceptionally(cause);
                                }
                                ctx.close();
                            }
                        });
                    }
                });
        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel();
        System.out.println("RPC Client 连接成功: " + host + ":" + port);
    }

    public Object send(Request request) throws Exception {
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException("客户端未连接");
        }
        pendingResponse = new CompletableFuture<>();
        channel.writeAndFlush(request);
        Response response = pendingResponse.get();
        if (response.getErrCode() != 0) {
            throw new RuntimeException(String.format("RPC error,code:%d,desc:%s", response.getErrCode(), response.getErrDesc()));
        }
        return response.getResult();
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
        group.shutdownGracefully();
        System.out.println("RPC Client 已关闭");
    }
}
