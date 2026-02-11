package com.lsm1998.rpc.example;

import com.lsm1998.rpc.provide.RpcServer;

public class ProvideApp {
    public static void main(String[] args) {
        RpcServer server = new RpcServer("0.0.0.0", 8080);
        server.register(new MyService());
        server.start();
    }
}
