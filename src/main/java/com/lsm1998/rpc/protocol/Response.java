package com.lsm1998.rpc.protocol;

import com.alibaba.fastjson.JSONObject;

import java.nio.charset.StandardCharsets;

public class Response {
    public byte[] encode() {
        return JSONObject.toJSONString(this).getBytes(StandardCharsets.UTF_8);
    }
}
