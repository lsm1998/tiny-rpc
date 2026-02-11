package com.lsm1998.rpc.provide;

import com.lsm1998.rpc.exceptions.MethodInternalException;
import com.lsm1998.rpc.protocol.Request;
import com.lsm1998.rpc.protocol.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProvideHandler extends SimpleChannelInboundHandler<Request> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) throws Exception {
        Response response = new Response();
        RpcService service = ProvideRegistry.getInstance().getService(request.getServiceName());
        if (service == null) {
            response.setErrCode(404);
            response.setErrDesc("Service not found: " + request.getServiceName());
        } else {
            try {
                Object result = service.invoke(request.getMethodName(), request.getParams());
                response.setResult(result);
            } catch (Exception e) {
                if (e instanceof NoSuchMethodException) {
                    response.setErrCode(404);
                    response.setErrDesc("Method not found: " + request.getMethodName());
                } else if (e instanceof MethodInternalException) {
                    response.setErrCode(((MethodInternalException) e).getCode());
                    response.setErrDesc("Internal error: " + e.getMessage());
                } else {
                    response.setErrCode(500);
                    response.setErrDesc("Invocation error: " + e.getMessage());
                }
            }
        }
        channelHandlerContext.writeAndFlush(response);
    }
}
