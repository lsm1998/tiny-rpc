package com.lsm1998.rpc.provide;

import com.lsm1998.rpc.annotations.InternalMethod;
import com.lsm1998.rpc.exceptions.MethodInternalException;

import java.lang.reflect.Method;

public class RpcService {
    private final Object serviceImpl;

    public RpcService(Object serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public Object invoke(String methodName, Class<?>[] paramTypes, Object[] args) throws Exception {
        Class<?> serviceClass = serviceImpl.getClass();
        Method method = serviceClass.getMethod(methodName, paramTypes);
        InternalMethod internalMethodAnnotation = method.getAnnotation(InternalMethod.class);
        if (internalMethodAnnotation != null) {
            throw new MethodInternalException("Method is internal and cannot be invoked: " + methodName);
        }
        return method.invoke(serviceImpl, args);
    }
}
