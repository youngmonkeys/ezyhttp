package com.tvd12.ezyhttp.server.management.test;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.management.HealthCheckController;
import com.tvd12.test.assertion.Asserts;

public class HealthCheckControllerTest {

    @Test
    public void healthCheck() {
        // given
        HealthCheckController sut = new HealthCheckController();
        
        // when
        ResponseEntity actual = sut.healthCheck();
        
        // then
        Asserts.assertEquals(actual, ResponseEntity.ok());
    }
}
