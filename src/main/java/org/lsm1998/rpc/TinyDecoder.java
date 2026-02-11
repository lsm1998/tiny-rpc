package org.lsm1998.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class TinyDecoder extends LengthFieldBasedFrameDecoder {

    public TinyDecoder() {
        super(1024 * 1024, 0, 4, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        Message message = new Message();
        byte[] logic = new byte[4];
        frame.readBytes(logic);

        // 校验魔数
        for (int i = 0; i < Message.MAGIC_NUMBER.length; i++) {
            if (logic[i] != Message.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Invalid magic number");
            }
        }

        message.setLogic(logic);
        message.setVersion(frame.readByte());
        message.setMessageType(frame.readByte());
        byte[] body = new byte[frame.readableBytes()];
        frame.readBytes(body);
        message.setBody(body);

        if (message.getMessageType() == Message.MessageType.REQUEST.getValue()) {
            return decodeRequest(message);
        } else if (message.getMessageType() == Message.MessageType.RESPONSE.getValue()) {
            return decodeResponse(message);
        } else {
            throw new IllegalArgumentException("Unknown message type: " + message.getMessageType());
        }
    }

    private Request decodeRequest(Message message) {
        // 这里可以根据实际需求进行反序列化
        return new Request();
    }

    private Response decodeResponse(Message message) {
        // 这里可以根据实际需求进行反序列化
        return new Response();
    }
}