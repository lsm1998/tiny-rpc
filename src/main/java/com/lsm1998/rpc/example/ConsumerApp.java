package com.lsm1998.rpc.example;

import com.lsm1998.rpc.consumer.RpcClient;

public class ConsumerApp {
    public static void main(String[] args) throws Exception {
        try (RpcClient client = new RpcClient("127.0.0.1", 8080)) {
            client.connect();

            MyService service = client.createProxy(MyService.class);
            String res = service.sayHello("lsm");
            System.out.println(res);
        }
    }
}
