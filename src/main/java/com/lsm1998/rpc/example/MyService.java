package com.lsm1998.rpc.example;

import com.lsm1998.rpc.annotations.InternalMethod;

public class MyService {
    public String sayHello(String name) throws InterruptedException {
        Thread.sleep(4000);
        return "Hello, " + name + "!";
    }

    @InternalMethod
    public int add(int a, int b) {
        return a + b;
    }
}
