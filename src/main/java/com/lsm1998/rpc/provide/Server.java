package com.lsm1998.rpc.provide;

public interface Server {
    void start();

    void close();

    void register(Object service);
}
