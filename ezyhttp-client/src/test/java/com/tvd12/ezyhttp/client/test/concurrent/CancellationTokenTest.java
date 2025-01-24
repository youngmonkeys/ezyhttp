package com.tvd12.ezyhttp.client.test.concurrent;

import com.tvd12.ezyhttp.client.concurrent.CancellationToken;
import org.testng.annotations.Test;

public class CancellationTokenTest {

    @Test
    public void test() {
        CancellationToken.ALWAYS_RUN.cancel();
    }
}
