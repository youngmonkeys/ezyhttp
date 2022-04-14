package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.boot.test.service.FileUploadService;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.server.core.annotation.*;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import lombok.AllArgsConstructor;

@Controller("/api/v1")
@AllArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;
    
    @Async
    @DoGet("/files/{file}")
    public void downloadGet(
        RequestArguments requestArguments,
        @PathVariable("file") String file,
        @RequestHeader("token") String token
    ) throws Exception {
        if (EzyStrings.isBlank(token)) {
            throw new HttpBadRequestException("token can not be null");
        }
        fileUploadService.download(requestArguments, file);
    }

    @Async
    @DoPost("/files/upload")
    public void uploadPost(
        RequestArguments requestArguments
    ) throws Exception {
        fileUploadService.accept(requestArguments.getRequest());
    }
}
