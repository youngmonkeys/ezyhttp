package com.tvd12.ezyhttp.server.core.test.handler;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.handler.RequestResponseWatcher;

public class RequestResponseWatcherTest {
    
    @Test
    public void test() {
        // given
        RequestResponseWatcher sut = new RequestResponseWatcher() {
        };
        
        // when
        // then
        sut.watchRequest(null, null);
        sut.watchResponse(null, null, null);
    }
}
