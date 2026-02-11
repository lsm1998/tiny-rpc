package com.lsm1998.rpc.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RefTest {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MyService service = new MyService();
        Class<?> serviceClass = service.getClass();
        Class<?>[] paramTypes = new Class<?>[]{int.class, int.class};
        Method method = serviceClass.getMethod("add", paramTypes);

        System.out.println(method.invoke(service, 100, 200));
    }
}
