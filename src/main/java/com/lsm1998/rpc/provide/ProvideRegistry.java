package com.lsm1998.rpc.provide;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideRegistry {

    private static final ProvideRegistry INSTANCE = new ProvideRegistry();

    private final Map<String, RpcService> serviceMap = new ConcurrentHashMap<>();

    private ProvideRegistry() {
    }

    public void register(Class<?> serviceInterface, Object serviceImpl) {
        this.serviceMap.put(serviceInterface.getName(), new RpcService(serviceImpl));
    }

    public RpcService getService(String serviceName) {
        return this.serviceMap.get(serviceName);
    }

    public static ProvideRegistry getInstance() {
        return INSTANCE;
    }
}
