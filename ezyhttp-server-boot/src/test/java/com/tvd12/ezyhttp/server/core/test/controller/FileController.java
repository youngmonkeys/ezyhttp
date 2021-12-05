package com.tvd12.ezyhttp.server.core.test.controller;

import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.boot.test.service.FileUploadService;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.server.core.annotation.Async;
import com.tvd12.ezyhttp.server.core.annotation.Controller;
import com.tvd12.ezyhttp.server.core.annotation.DoGet;
import com.tvd12.ezyhttp.server.core.annotation.PathVariable;
import com.tvd12.ezyhttp.server.core.annotation.RequestHeader;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;

import lombok.AllArgsConstructor;

@Controller("/api/v1")
@AllArgsConstructor
public class FileController {

    private final FileUploadService fileUploadService;
    
    @Async
    @DoGet("/files/{file}")
    public void uploadPost(
        RequestArguments requestArguments,
        @PathVariable("file") String file,
        @RequestHeader("token") String token
    ) throws Exception {
        if (EzyStrings.isBlank(token)) {
            throw new HttpBadRequestException("token can not be null");
        }
        fileUploadService.download(requestArguments, file);
    }
}
