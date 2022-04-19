package com.tvd12.ezyhttp.server.core.test.asm;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.asm.AsmUncaughtExceptionHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

public class AsmUncaughtExceptionHandlerTest {

    @Test
    public void test() {
        // given
        AsmUncaughtExceptionHandler sut = new AsmUncaughtExceptionHandler() {
            @Override
            public Object handleException(RequestArguments arguments, Exception exception) {
                return null;
            }
            
        };
        
        // when
        // then
        sut.setExceptionHandler(null);
    }
}
