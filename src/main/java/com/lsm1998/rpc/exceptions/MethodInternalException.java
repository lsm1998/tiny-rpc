package com.lsm1998.rpc.exceptions;

public class MethodInternalException extends RuntimeException {
    public MethodInternalException(String message) {
        super(message);
    }

    public int getCode() {
        return 403;
    }
}
