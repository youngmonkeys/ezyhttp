package com.tvd12.ezyhttp.core.test.resources;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.resources.ActualContentTypeDetector;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;

public class ActualContentTypeDetectorTest extends BaseTest {
    
    @Test
    public void noNeedToDetectTest() {
        // given
        ActualContentTypeDetector sut = ActualContentTypeDetector.getInstance();
        String resourcePath = "/hello.world/filename.png";
        String originalContentType = ContentTypes.IMAGE_PNG;
        
        // when
        String actualContentType = sut.detect(resourcePath, originalContentType);
        
        // then
        Asserts.assertEquals(actualContentType, originalContentType);
    }

    @Test
    public void detectNonEmptyTwoPartsExtensionTest() {
        // given
        ActualContentTypeDetector sut = ActualContentTypeDetector.getInstance();
        String resourcePath = "/hello.world/filename.wasm.gz";
        String originalContentType = ContentTypes.GZIP;
        String expectedContentType = ContentTypes.APPLICATION_WASM;

        // when
        String actualContentType = sut.detect(resourcePath, originalContentType);
        
        // then
        Asserts.assertEquals(actualContentType, expectedContentType);
    }
    
    @Test
    public void detectEmptyTwoPartsExtensionTest() {
        // given
        ActualContentTypeDetector sut = ActualContentTypeDetector.getInstance();
        String resourcePathLinux = "/hello.world.com/filename.gz";
        String resourcePathWindow = "\\hello.world.com\\filename.gz";
        String originalContentType = ContentTypes.GZIP;

        // when
        String actualContentTypeLinux = sut.detect(resourcePathLinux, originalContentType);
        String actualContentTypeWindow = sut.detect(resourcePathWindow, originalContentType);

        // then
        Asserts.assertEquals(actualContentTypeLinux, originalContentType);
        Asserts.assertEquals(actualContentTypeWindow, originalContentType);
    }

    @Test
    public void detectEmptyResourcePathTest() {
        // given
        ActualContentTypeDetector sut = ActualContentTypeDetector.getInstance();
        String resourcePath = "";
        String originalContentType = ContentTypes.GZIP;

        // when
        String actualContentType = sut.detect(resourcePath, originalContentType);

        // then
        Asserts.assertEquals(actualContentType, originalContentType);
    }
}
