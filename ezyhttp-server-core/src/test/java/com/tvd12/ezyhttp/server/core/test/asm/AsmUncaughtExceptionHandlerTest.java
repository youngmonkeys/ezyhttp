package com.tvd12.ezyhttp.server.core.test.asm;

import com.tvd12.ezyhttp.server.core.asm.AsmUncaughtExceptionHandler;
import org.testng.annotations.Test;

public class AsmUncaughtExceptionHandlerTest {

    @Test
    public void test() {
        // given
        AsmUncaughtExceptionHandler sut = (arguments, exception) -> null;

        // when
        // then
        sut.setExceptionHandler(null);
    }
}
