package com.tvd12.ezyhttp.server.tomcat.test.limit;

import com.tvd12.ezyhttp.server.core.ApplicationContext;
import com.tvd12.ezyhttp.server.core.ApplicationContextBuilder;
import com.tvd12.ezyhttp.server.tomcat.TomcatApplicationBootstrap;
import com.tvd12.ezyhttp.server.tomcat.test.limit.HttpCall.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class TomcatRequestLimitTest {

    private static final int PAYLOAD_TOO_LARGE = 413;
    private static final int NOT_ACCEPTABLE = 406;
    private static final int OK = 200;

    private ApplicationContext applicationContext;
    private TomcatApplicationBootstrap bootstrap;
    private String baseUrl;

    @BeforeClass
    public void setUp() throws Exception {
        Path multipartDir = Files.createTempDirectory("ezyhttp-tomcat-limit");
        int port = freePort();
        baseUrl = "http://127.0.0.1:" + port;
        applicationContext = new ApplicationContextBuilder()
            .scan(getClass().getPackage().getName())
            .build();
        bootstrap = new TomcatApplicationBootstrap();
        bootstrap.setHost("127.0.0.1");
        bootstrap.setPort(port);
        bootstrap.setManagementEnable(false);
        bootstrap.setMaxRequestBodySize("1KB");
        bootstrap.setMultipartLocation(multipartDir.toString());
        bootstrap.setMultipartFileSizeThreshold("1KB");
        bootstrap.setMultipartMaxFileSize("16KB");
        bootstrap.setMultipartMaxRequestSize("16KB");
        bootstrap.setMaxRequestHeaderSize("4KB");
        bootstrap.setMaxRequestParameterCount(5);
        bootstrap.setMaxRequestHeaderCount(20);
        bootstrap.setMaxCookieCount(3);
        bootstrap.setContextPath(multipartDir.toString());
        bootstrap.start();
    }

    @AfterClass
    public void tearDown() {
        if (bootstrap != null) {
            bootstrap.stop();
        }
        if (applicationContext != null) {
            applicationContext.destroy();
        }
    }

    @BeforeMethod
    public void resetState() {
        LimitState.reset();
    }

    @Test
    public void jsonWithinBodyLimitAcceptedTest() throws Exception {
        Response response = HttpCall.post(
            baseUrl + "/limit/json",
            "application/json",
            json(HttpCall.repeat('a', 200)),
            false
        );

        Assert.assertEquals(response.status, OK, response.toString());
        Assert.assertTrue(response.body.contains("json:200"), response.toString());
        Assert.assertEquals(LimitState.HANDLED_JSONS.get(), 1);
    }

    @Test
    public void jsonOverBodyLimitRejectedWithPayloadTooLargeTest() throws Exception {
        Response response = HttpCall.post(
            baseUrl + "/limit/json",
            "application/json",
            json(HttpCall.repeat('a', 2000)),
            false
        );

        Assert.assertEquals(response.status, PAYLOAD_TOO_LARGE, response.toString());
        Assert.assertEquals(LimitState.HANDLED_JSONS.get(), 0);
    }

    @Test
    public void chunkedJsonOverBodyLimitNeverReachesHandlerTest() throws Exception {
        Response response = HttpCall.post(
            baseUrl + "/limit/json",
            "application/json",
            json(HttpCall.repeat('a', 2000)),
            true
        );

        Assert.assertTrue(
            response.status >= 400 || response.status == Response.CONNECTION_CLOSED,
            response.toString()
        );
        Assert.assertEquals(LimitState.HANDLED_JSONS.get(), 0);
    }

    @Test
    public void uploadLargerThanBodyLimitButWithinUploadLimitAcceptedTest() throws Exception {
        Response response = HttpCall.post(
            baseUrl + "/limit/upload?folder=avatars",
            HttpCall.multipartContentType(),
            uploadBody(8 * 1024),
            false,
            Collections.singletonMap("token", "valid")
        );

        Assert.assertEquals(response.status, OK, response.toString());
        Assert.assertTrue(
            response.body.contains("upload:avatars:my photo:8192"),
            response.toString()
        );
        Assert.assertEquals(LimitState.HANDLED_UPLOADS.get(), 1);
    }

    @Test
    public void uploadOverUploadLimitRejectedWithPayloadTooLargeTest() throws Exception {
        Response response = HttpCall.post(
            baseUrl + "/limit/upload?folder=avatars",
            HttpCall.multipartContentType(),
            uploadBody(20 * 1024),
            false,
            Collections.singletonMap("token", "valid")
        );

        Assert.assertTrue(
            response.status == PAYLOAD_TOO_LARGE
                || response.status == Response.CONNECTION_CLOSED,
            response.toString()
        );
        Assert.assertEquals(LimitState.HANDLED_UPLOADS.get(), 0);
    }

    @Test
    public void rejectedUploadInterceptorSeesOnlyQueryParametersTest() throws Exception {
        Response response = HttpCall.post(
            baseUrl + "/limit/upload?folder=avatars",
            HttpCall.multipartContentType(),
            uploadBody(8 * 1024),
            false
        );

        Assert.assertEquals(response.status, NOT_ACCEPTABLE, response.toString());
        Assert.assertEquals(LimitState.HANDLED_UPLOADS.get(), 0);
        Assert.assertEquals(LimitState.seenFolder, "avatars");
        Assert.assertNull(LimitState.seenDescription);
        Assert.assertNull(LimitState.seenRawDescription);
    }

    @Test
    public void acceptedUploadInterceptorStillCannotSeeBodyFieldsTest() throws Exception {
        Response response = HttpCall.post(
            baseUrl + "/limit/upload?folder=avatars",
            HttpCall.multipartContentType(),
            uploadBody(1024),
            false,
            Collections.singletonMap("token", "valid")
        );

        Assert.assertEquals(response.status, OK, response.toString());
        Assert.assertEquals(LimitState.seenFolder, "avatars");
        Assert.assertNull(LimitState.seenDescription);
        Assert.assertTrue(response.body.contains("my photo"), response.toString());
    }

    @Test
    public void headerWithinLimitAcceptedTest() throws Exception {
        Response response = HttpCall.get(
            baseUrl + "/limit/ping",
            Collections.singletonMap("X-Data", HttpCall.repeat('a', 1024))
        );

        Assert.assertEquals(response.status, OK, response.toString());
    }

    @Test
    public void headerOverLimitRejectedTest() throws Exception {
        Response response = HttpCall.get(
            baseUrl + "/limit/ping",
            Collections.singletonMap("X-Data", HttpCall.repeat('a', 6 * 1024))
        );

        Assert.assertTrue(
            response.status == 431 || response.status == 400,
            response.toString()
        );
    }

    @Test
    public void tooManyFormParametersNotAllAcceptedTest() throws Exception {
        StringBuilder form = new StringBuilder();
        for (int i = 0; i < 10; ++i) {
            if (i > 0) {
                form.append('&');
            }
            form.append('p').append(i).append('=').append(i);
        }

        Response response = HttpCall.post(
            baseUrl + "/limit/params",
            "application/x-www-form-urlencoded",
            form.toString().getBytes(StandardCharsets.UTF_8),
            false
        );

        Assert.assertFalse(
            response.status == OK && response.body.contains("params:10"),
            response.toString()
        );
    }

    @Test
    public void tooManyHeadersRejectedTest() throws Exception {
        Map<String, String> headers = new LinkedHashMap<>();
        for (int i = 0; i < 30; ++i) {
            headers.put("X-Header-" + i, String.valueOf(i));
        }

        Response response = HttpCall.get(baseUrl + "/limit/ping", headers);

        Assert.assertEquals(response.status, 400, response.toString());
    }

    @Test
    public void cookiesWithinLimitAcceptedTest() throws Exception {
        Response response = HttpCall.get(
            baseUrl + "/limit/cookies",
            Collections.singletonMap("Cookie", "c0=0; c1=1")
        );

        Assert.assertEquals(response.status, OK, response.toString());
        Assert.assertTrue(response.body.contains("cookies:2"), response.toString());
    }

    @Test
    public void tooManyCookiesNotAllAcceptedTest() throws Exception {
        Response response = HttpCall.get(
            baseUrl + "/limit/cookies",
            Collections.singletonMap("Cookie", "c0=0; c1=1; c2=2; c3=3; c4=4; c5=5")
        );

        Assert.assertFalse(
            response.status == OK && response.body.contains("cookies:6"),
            response.toString()
        );
    }

    private static byte[] json(String who) {
        return ("{\"who\":\"" + who + "\"}").getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] uploadBody(int fileSize) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("description", "my photo");
        return HttpCall.multipart(fields, "file", HttpCall.bytes(fileSize));
    }

    private static int freePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }
}
