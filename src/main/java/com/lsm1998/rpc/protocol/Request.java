package com.lsm1998.rpc.protocol;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Request {
    private static final AtomicInteger REQ_CONTINUE = new AtomicInteger(1);

    private int requestId;
    private String serviceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;

    public void setRequestId() {
        this.requestId = REQ_CONTINUE.incrementAndGet();
    }

    public byte[] encode() {
        return JSONObject.toJSONString(this).getBytes(StandardCharsets.UTF_8);
    }
}
