package com.lsm1998.rpc.example;

import com.lsm1998.rpc.consumer.RpcClient;
import com.lsm1998.rpc.protocol.Request;

public class ConsumerApp {
    public static void main(String[] args) throws Exception {
        try (RpcClient client = new RpcClient("127.0.0.1", 8080)) {
            client.connect();
            Request request = new Request();
            request.setRequestId("12345");
            request.setServiceName("com.lsm1998.rpc.example.MyService");
            request.setMethodName("add");
            request.setParamTypes(new Class<?>[]{int.class, int.class});
            request.setParams(new Object[]{100, 200});
            Object response = client.send(request);
            System.out.println("收到响应: " + response);
        }
    }
}
