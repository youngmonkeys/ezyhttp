package com.tvd12.ezyhttp.server.boot.test.controller;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tvd12.ezyhttp.server.boot.test.service.FileUploadService;
import com.tvd12.ezyhttp.server.core.annotation.Async;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.DoPost;
import com.tvd12.ezyhttp.server.core.annotation.RequestBody;
import com.tvd12.ezyhttp.server.core.annotation.RequestParam;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.view.View;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Controller("/view")
@AllArgsConstructor
public class ViewController {

    private final FileUploadService fileUploadService;

    @DoGet("/home")
    public View home() {
        return View.of("home");
    }

    @DoGet("/greet")
    public View home(@RequestParam String who) {
        return View.builder()
            .template("greet/greet")
            .addVariable("who", who)
            .addVariable("welcome", new Welcome("Welcome " + who))
            .addVariable("hi", Collections.singletonMap("message", "Hi " + who))
            .build();
    }

    @DoGet("/upload")
    public View uploadGet() {
        return View.of("upload/upload");
    }

    @Async
    @DoPost("/upload")
    public void uploadPost(
        HttpServletRequest request,
        HttpServletResponse response,
        @RequestBody FileUpload data,
        @RequestParam("name") String fileName,
        RequestArguments requestArguments
    ) throws Exception {
        Part filePart = request.getPart("file");
        if (filePart.getSize() > 0) {
            fileUploadService.accept(request, filePart, () -> {
                try {
                    response.sendRedirect("/view/upload");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Welcome {
        private String message;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FileUpload {
        private String name;
    }
}
