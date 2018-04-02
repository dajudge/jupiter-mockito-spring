package com.dajudge.playground;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = SpringMockitoTest.TestBean.class)
class SpringMockitoTest {
    @Autowired
    private TestBean testBean;
    @Mock
    private InterfaceToMock mock;

    @Test
    void mockito_integrates_with_spring() {
        when(mock.doIt()).thenReturn("I did it!");
        assertEquals("I did it!", testBean.callMock());
    }

    @Service
    public static class TestBean {
        @Autowired
        private InterfaceToMock mock;

        public String callMock() {
            return mock.doIt();
        }
    }

    public interface InterfaceToMock {
        String doIt();
    }
}