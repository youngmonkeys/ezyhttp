package com.tvd12.ezyhttp.client.test;

import java.io.File;

import com.tvd12.ezyhttp.client.HttpClientProxy;
import com.tvd12.ezyhttp.client.request.DownloadRequest;
import com.tvd12.ezyhttp.core.data.MultiValueMap;

public class DownloadWithTokenTest {

    public static void main(String[] args) throws Exception {
        // given
        String fileUrl = "http://localhost:8083/api/v1/files/ansible.jpg";
        DownloadRequest request = new DownloadRequest()
            .setFileURL(fileUrl)
            .setConnectTimeout(5000)
            .setReadTimeout(5000)
            .setHeaders(
                MultiValueMap.builder()
                    .setValue("token", "")
                    .build()
            );
        
        HttpClientProxy sut = HttpClientProxy.builder()
                .requestQueueCapacity(1)
                .threadPoolSize(1)
                .build();
        
        // when
        String fileName = sut.download(request, new File("test-output/no-commit"));
        System.out.println(fileName);
        
        // then
        sut.close();
        sut.stop();
    }
    
}
