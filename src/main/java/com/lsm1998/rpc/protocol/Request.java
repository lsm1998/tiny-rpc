package com.lsm1998.rpc.protocol;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class Request {
    private String requestId;
    private String serviceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;

    public byte[] encode() {
        return JSONObject.toJSONString(this).getBytes(StandardCharsets.UTF_8);
    }
}
