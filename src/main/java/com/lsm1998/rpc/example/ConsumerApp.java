package com.lsm1998.rpc.example;

import com.lsm1998.rpc.consumer.RpcClient;
import com.lsm1998.rpc.protocol.Request;
import com.lsm1998.rpc.protocol.Response;

public class ConsumerApp {
    public static void main(String[] args) throws Exception {
        try (RpcClient client = new RpcClient("127.0.0.1", 8080)) {
            client.connect();
            Request request = new Request();
            request.setRequestId("12345");
            request.setServiceName("com.lsm1998.rpc.example.MyService");
            request.setMethodName("sayHello");
            request.setParamTypes(new Class<?>[]{String.class});
            request.setParams(new Object[]{"Hello RPC"});
            Response response = client.send(request);
            System.out.println("收到响应: " + response);
        }
    }
}
