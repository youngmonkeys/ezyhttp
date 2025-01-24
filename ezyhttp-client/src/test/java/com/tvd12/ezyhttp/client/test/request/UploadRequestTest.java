package com.tvd12.ezyhttp.client.test.request;

import com.tvd12.ezyhttp.client.request.UploadRequest;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URL;

public class UploadRequestTest {

    @Test
    public void test() throws Exception {
        // given
        String url = "https://youngmonkeys.org";
        MultiValueMap headers = MultiValueMap.builder()
            .setValue("access_token", "token")
            .build();

        // when
        UploadRequest actual = new UploadRequest()
            .setURL(URI.create(url))
            .setURL(new URL(url))
            .setMethod(null)
            .setMethod(HttpMethod.POST)
            .setReadTimeout(1)
            .setConnectTimeout(2)
            .setHeaders(headers);

        // then
        Asserts.assertEquals(actual.getURL(), url);
        Asserts.assertEquals(actual.getMethod(), HttpMethod.POST);
        Asserts.assertEquals(actual.getReadTimeout(), 1);
        Asserts.assertEquals(actual.getConnectTimeout(), 2);
        Asserts.assertEquals(actual.getHeaders(), headers);
    }
}
