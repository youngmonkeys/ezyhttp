package com.tvd12.ezyhttp.server.core.test.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.util.BannerPrinter;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodInvoker;

public class BannerPrinterTest {

    @Test
    public void getBannerBytesSuccessButEmpty() throws Exception {
        // given
        BannerPrinter sut = new BannerPrinter();
        
        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read(any())).thenThrow(new IOException("just test"));
        
        // when
        byte[] bytes = MethodInvoker.create()
                .object(sut)
                .method("getBannerBytes")
                .param(InputStream.class, inputStream)
                .invoke(byte[].class);
        
        // then
        Asserts.assertEquals(0, bytes.length);
        
        verify(inputStream, times(1)).read(any());
    }
}
