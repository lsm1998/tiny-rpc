package com.lsm1998.rpc.protocol;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public class Response {
    private Object result;

    public byte[] encode() {
        return JSONObject.toJSONString(this).getBytes(StandardCharsets.UTF_8);
    }
}
