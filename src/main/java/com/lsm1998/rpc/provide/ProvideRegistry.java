package com.lsm1998.rpc.provide;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProvideRegistry {

    private static final ProvideRegistry INSTANCE = new ProvideRegistry();

    private final Map<String, RpcService> serviceMap = new ConcurrentHashMap<>();

    private ProvideRegistry() {
    }

    public void register(Class<?> serviceInterface, Object serviceImpl) {
        RpcService service = this.serviceMap.putIfAbsent(serviceInterface.getName(), new RpcService(serviceImpl));
        if (service != null) {
            throw new RuntimeException("Service already registered: " + serviceInterface.getName());
        }
    }

    public RpcService getService(String serviceName) {
        return this.serviceMap.get(serviceName);
    }

    public static ProvideRegistry getInstance() {
        return INSTANCE;
    }
}
