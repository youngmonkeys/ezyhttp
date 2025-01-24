package com.tvd12.ezyhttp.client.test.concurrent;

import com.tvd12.ezyhttp.client.concurrent.UploadCancellationToken;
import org.testng.annotations.Test;

public class UploadCancellationTokenTest {

    @Test
    public void test() {
        UploadCancellationToken.ALWAYS_RUN.cancel();
    }
}
