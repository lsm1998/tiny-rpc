package com.lsm1998.rpc.codec;

import com.lsm1998.rpc.protocol.Message;
import com.lsm1998.rpc.protocol.Response;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ResponseEncoder extends MessageToByteEncoder<Response> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Response response, ByteBuf byteBuf) throws Exception {
        byte[] body = response.encode();
        byteBuf.writeInt(body.length + 6); // 4字节长度 + 4字节魔数 + 1字节版本 + 1字节消息类型
        byteBuf.writeBytes(Message.MAGIC_NUMBER);
        byteBuf.writeByte(1);
        byteBuf.writeByte(Message.MessageType.RESPONSE.getValue());
        byteBuf.writeBytes(body);
    }
}
