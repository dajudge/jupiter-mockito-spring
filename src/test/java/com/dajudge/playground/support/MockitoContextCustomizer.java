package com.dajudge.playground.support;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class MockitoContextCustomizer implements ContextCustomizer {
    private final List<Field> mockFields;

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final MockitoContextCustomizer that = (MockitoContextCustomizer) o;

        return mockFields != null ? mockFields.equals(that.mockFields) : that.mockFields == null;
    }

    @Override
    public int hashCode() {
        return mockFields != null ? mockFields.hashCode() : 0;
    }

    public MockitoContextCustomizer(final List<Field> mockFields) {
        this.mockFields = mockFields;
    }

    @Override
    public void customizeContext(
            final ConfigurableApplicationContext context,
            final MergedContextConfiguration mergedConfig
    ) {
        mockFields.forEach(field -> {
            field.setAccessible(true);
            final Object proxy = createProxyFor(field);
            final String beanName = "mock" + field.getType().getSimpleName();
            context.getBeanFactory().registerSingleton(beanName, proxy);
        });
    }

    private Object createProxyFor(final Field field) {
        try {
            final ProxyFactory proxyFactory = new ProxyFactory();
            proxyFactory.setFilter(m -> !m.getDeclaringClass().equals(Object.class));
            if (field.getType().isInterface()) {
                proxyFactory.setInterfaces(new Class<?>[]{field.getType(), MockProxy.class});
            } else {
                proxyFactory.setSuperclass(field.getType());
                proxyFactory.setInterfaces(new Class<?>[]{MockProxy.class});
            }
            final Object proxy = proxyFactory.create(null, null);
            final MethodHandler proxyHandler = new MethodHandler() {
                private MethodHandler mockHandler;

                @Override
                public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
                    if (thisMethod.getDeclaringClass() == MockProxy.class) {
                        if (thisMethod.getName().equals("setMockHandler")) {
                            mockHandler = (MethodHandler) args[0];
                            return null;
                        } else if (thisMethod.getName().equals("getMockField")) {
                            return field;
                        }
                    }
                    return mockHandler.invoke(self, thisMethod, proceed, args);
                }
            };
            ((Proxy) proxy).setHandler(proxyHandler);
            return proxy;
        } catch (final NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException("Failed to create mock delegation proxy", e);
        }
    }
}
