package com.tvd12.ezyhttp.client.test.request;

import com.tvd12.ezyhttp.client.request.DownloadRequest;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;

public class DownloadRequestTest {

    @Test
    public void test() throws Exception {
        // given
        String fileURL = "https://youngmonkeys.org";
        int readTimeout = RandomUtil.randomInt();
        int connectionTimeout = RandomUtil.randomInt();
        DownloadRequest sut = new DownloadRequest();

        MultiValueMap headers = MultiValueMap.builder()
            .setValue("hello", "world")
            .build();

        sut.setFileURL(fileURL);
        sut.setFileURL(new URL(fileURL));
        sut.setFileURL(URI.create(fileURL));
        sut.setReadTimeout(readTimeout);
        sut.setConnectTimeout(connectionTimeout);
        sut.setHeaders(headers);

        // when
        // then
        Asserts.assertEquals(fileURL, sut.getFileURL());
        Asserts.assertEquals(readTimeout, sut.getReadTimeout());
        Asserts.assertEquals(connectionTimeout, sut.getConnectTimeout());
        Asserts.assertEquals(headers, sut.getHeaders());
    }
}
