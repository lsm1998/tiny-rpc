package com.lsm1998.rpc.consumer;

import com.lsm1998.rpc.protocol.Request;
import lombok.Getter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import net.sf.cglib.proxy.Enhancer;

import java.lang.reflect.Method;

public class RpcProxy<T> {
    private final RpcClient client;
    @Getter
    private final T proxyInstance;
    private final Class<T> serviceClass;

    public RpcProxy(RpcClient client, Class<T> serviceClass) {
        this.client = client;
        this.serviceClass = serviceClass;

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(serviceClass);
        enhancer.setCallback(new RpcInterceptor());

        @SuppressWarnings("unchecked")
        T instance = (T) enhancer.create();
        this.proxyInstance = instance;
    }

    class RpcInterceptor implements MethodInterceptor {
        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            Request request = new Request();
            request.setRequestId();
            request.setServiceName(serviceClass.getName());
            request.setMethodName(method.getName());
            request.setParamTypes(method.getParameterTypes());
            request.setParams(objects);
            return client.send(request);
        }
    }
}
