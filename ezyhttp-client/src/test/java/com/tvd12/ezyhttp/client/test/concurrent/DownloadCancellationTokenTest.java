package com.tvd12.ezyhttp.client.test.concurrent;

import com.tvd12.ezyhttp.client.concurrent.DownloadCancellationToken;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class DownloadCancellationTokenTest {

    @Test
    public void alwayTrueTest() {
        // given
        DownloadCancellationToken.ALWAYS_RUN.cancel();

        // when
        // then
        Asserts.assertFalse(DownloadCancellationToken.ALWAYS_RUN.isCancelled());
    }

    @Test
    public void cancelTest() {
        // given
        DownloadCancellationToken sut = new DownloadCancellationToken();

        // when
        sut.cancel();

        // then
        Asserts.assertTrue(sut.isCancelled());
    }
}
