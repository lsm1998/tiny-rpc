package com.lsm1998.rpc.consumer;

import com.lsm1998.rpc.codec.RequestEncoder;
import com.lsm1998.rpc.codec.TinyDecoder;
import com.lsm1998.rpc.constant.RpcCode;
import com.lsm1998.rpc.protocol.Request;
import com.lsm1998.rpc.protocol.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RpcClient implements AutoCloseable {
    private final String host;
    private final int port;
    private final EventLoopGroup group;
    private Channel channel;
    private CompletableFuture<Response> pendingResponse;

    @Setter
    private int readTimeoutMillis = 0;

    @Setter
    private int connectTimeoutMillis = 0;

    @Setter
    private int writeTimeoutMillis = 0;

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
                        if (writeTimeoutMillis > 0) {
                            ch.pipeline().addLast(new WriteTimeoutHandler(writeTimeoutMillis, TimeUnit.MILLISECONDS));
                        }
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
        if (connectTimeoutMillis > 0) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis);
        }
        ChannelFuture future = bootstrap.connect(host, port).sync();
        this.channel = future.channel();
    }

    public Object send(Request request) throws Exception {
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException("客户端未连接");
        }
        pendingResponse = new CompletableFuture<>();
        channel.writeAndFlush(request);
        if (readTimeoutMillis > 0) {
            pendingResponse.orTimeout(readTimeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
        Response response = pendingResponse.get();
        if (response.getErrCode() != RpcCode.CODE_OK) {
            throw new RuntimeException(String.format("RPC error,code:%d,desc:%s", response.getErrCode(), response.getErrDesc()));
        }
        return response.getResult();
    }

    public <T> T createProxy(Class<T> serviceClass) {
        RpcProxy<T> proxy = new RpcProxy<>(this, serviceClass);
        return proxy.getProxyInstance();
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
