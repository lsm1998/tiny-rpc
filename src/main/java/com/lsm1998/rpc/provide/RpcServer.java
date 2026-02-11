package com.lsm1998.rpc.provide;

import com.lsm1998.rpc.Server;
import com.lsm1998.rpc.codec.ResponseEncoder;
import com.lsm1998.rpc.codec.TinyDecoder;
import com.lsm1998.rpc.protocol.Request;
import com.lsm1998.rpc.protocol.Response;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class RpcServer implements Server {
    private final String host;
    private final int port;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    public RpcServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        this.workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    }

    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new TinyDecoder());
                    ch.pipeline().addLast(new ResponseEncoder());
                    ch.pipeline().addLast(new SimpleChannelInboundHandler<Request>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
                            System.out.println(request);

                            Response response = new Response();
                            response.setResult("hello client, your requestId is " + request.getRequestId());
                            channelHandlerContext.writeAndFlush(response);
                        }
                    });
                }
            });
            ChannelFuture future = bootstrap.bind(host, port).sync();
            System.out.println("RPC Server 启动成功，监听端口: " + port);
            // 等待服务器 socket 关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
            System.err.println("服务器运行异常: " + e.getMessage());
        } finally {
            close();
        }
    }

    @Override
    public void close() {
        if (bossGroup != null) bossGroup.shutdownGracefully();
        if (workerGroup != null) workerGroup.shutdownGracefully();
        System.out.println("RPC Server 已关闭");
    }
}
