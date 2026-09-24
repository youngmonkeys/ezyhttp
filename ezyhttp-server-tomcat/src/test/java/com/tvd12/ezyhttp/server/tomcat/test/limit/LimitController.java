package com.tvd12.ezyhttp.server.tomcat.test.limit;

import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Map;

@Controller("/limit")
public class LimitController {

    @DoPost("/json")
    public String json(@RequestBody LimitBody body) {
        LimitState.HANDLED_JSONS.incrementAndGet();
        return "json:" + body.getWho().length();
    }

    @DoPost("/upload")
    public String upload(
        RequestArguments arguments,
        @RequestParam("folder") String folder,
        @RequestParam("description") String description
    ) throws Exception {
        Part file = arguments.getRequest().getPart("file");
        LimitState.HANDLED_UPLOADS.incrementAndGet();
        return "upload:" + folder + ":" + description + ":" + file.getSize();
    }

    @DoPost("/params")
    public String params(RequestArguments arguments) {
        Map<String, String> parameters = arguments.getParameters();
        return "params:" + (parameters != null ? parameters.size() : 0);
    }

    @DoGet("/cookies")
    public String cookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        return "cookies:" + (cookies != null ? cookies.length : 0);
    }

    @DoGet("/ping")
    public String ping() {
        return "pong";
    }
}
