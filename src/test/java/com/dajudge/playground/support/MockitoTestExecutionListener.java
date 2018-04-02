package com.dajudge.playground.support;

import javassist.util.proxy.MethodHandler;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

import java.lang.reflect.InvocationTargetException;

public class MockitoTestExecutionListener implements TestExecutionListener {
    @Override
    public void prepareTestInstance(final TestContext testContext) throws Exception {
        testContext.getApplicationContext().getBeansOfType(MockProxy.class).values().stream()
                .forEach(proxy -> {
                    final MethodHandler mockHandler = (self, thisMethod, proceed, args) -> {
                        try {
                            final Object mock = proxy.getMockField().get(testContext.getTestInstance());
                            return thisMethod.invoke(mock, args);
                        } catch (InvocationTargetException e) {
                            throw e;
                        }
                    };
                    proxy.setMockHandler(mockHandler);
                });
    }

}
