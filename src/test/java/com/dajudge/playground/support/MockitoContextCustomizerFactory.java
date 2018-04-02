package com.dajudge.playground.support;

import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.ContextCustomizerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

public class MockitoContextCustomizerFactory implements ContextCustomizerFactory {
    @Override
    public ContextCustomizer createContextCustomizer(
            final Class<?> testClass,
            final List<ContextConfigurationAttributes> configAttributes
    ) {
        final List<Field> mockFields = getAllFieldsList(testClass).stream()
                .filter(this::hasMockitoAnnotation)
                .collect(toList());
        return new MockitoContextCustomizer(mockFields);
    }

    private boolean hasMockitoAnnotation(final Field field) {
        return Stream.of(Mock.class, Spy.class).anyMatch(a -> field.getAnnotation(a) != null);
    }

}
