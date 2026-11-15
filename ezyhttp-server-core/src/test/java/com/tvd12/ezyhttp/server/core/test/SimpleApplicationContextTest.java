package com.tvd12.ezyhttp.server.core.test;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.bean.EzyBeanContext;
import com.tvd12.ezyfox.util.EzyDestroyable;
import com.tvd12.ezyhttp.server.core.SimpleApplicationContext;

public class SimpleApplicationContextTest {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void destroyWithErrorComponentTest() {
        // given
        EzyDestroyable failedComponent = mock(EzyDestroyable.class);
        doThrow(new IllegalStateException("test"))
            .when(failedComponent)
            .destroy();
        EzyDestroyable succeededComponent = mock(EzyDestroyable.class);

        EzyBeanContext beanContext = mock(EzyBeanContext.class);
        List components = Arrays.asList(failedComponent, succeededComponent);
        when(beanContext.getSingletonsOf(EzyDestroyable.class))
            .thenReturn(components);

        SimpleApplicationContext sut = new SimpleApplicationContext();
        sut.setBeanContext(beanContext);

        // when
        sut.destroy();

        // then
        verify(beanContext, times(1)).getSingletonsOf(EzyDestroyable.class);
        verify(failedComponent, times(1)).destroy();
        verify(succeededComponent, times(1)).destroy();
    }
}
