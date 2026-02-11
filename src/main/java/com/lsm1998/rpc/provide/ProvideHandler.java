package com.lsm1998.rpc.provide;

import com.lsm1998.rpc.constant.RpcCode;
import com.lsm1998.rpc.exceptions.MethodInternalException;
import com.lsm1998.rpc.protocol.Request;
import com.lsm1998.rpc.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProvideHandler extends SimpleChannelInboundHandler<Request> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        Response response = new Response();
        response.setErrCode(RpcCode.CODE_OK);
        RpcService service = ProvideRegistry.getInstance().getService(request.getServiceName());
        if (service == null) {
            response.setErrCode(RpcCode.CODE_NOT_FOUND);
            response.setErrDesc("Service not found: " + request.getServiceName());
        } else {
            try {
                Object result = service.invoke(request.getMethodName(), request.getParamTypes(), request.getParams());
                response.setResult(result);
            } catch (Exception e) {
                if (e instanceof NoSuchMethodException) {
                    response.setErrCode(RpcCode.CODE_NOT_FOUND);
                    response.setErrDesc("Method not found: " + request.getMethodName());
                } else if (e instanceof MethodInternalException) {
                    response.setErrCode(((MethodInternalException) e).getCode());
                    response.setErrDesc("Internal error: " + e.getMessage());
                } else {
                    response.setErrCode(RpcCode.CODE_SERVER_ERROR);
                    response.setErrDesc("Invocation error: " + e.getMessage());
                }
            }
        }
        channelHandlerContext.writeAndFlush(response);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接建立: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("连接断开: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("连接异常: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
