package com.lsm1998.rpc.provide;

import java.lang.reflect.Method;

public class RpcService {
    private final Object serviceImpl;

    public RpcService(Object serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public Object invoke(String methodName, Object[] args) throws Exception {
        Class<?> serviceClass = serviceImpl.getClass();
        Class<?>[] argTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        Method method = serviceClass.getMethod(methodName, argTypes);
        return method.invoke(serviceImpl, args);
    }
}
