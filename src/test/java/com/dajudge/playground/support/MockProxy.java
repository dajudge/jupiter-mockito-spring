package com.dajudge.playground.support;

import javassist.util.proxy.MethodHandler;

import java.lang.reflect.Field;

public interface MockProxy {
    void setMockHandler(MethodHandler methodHandler);

    Field getMockField();
}
