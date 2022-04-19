package com.tvd12.ezyhttp.server.boot.test.service;

import java.io.File;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.tvd12.ezyfox.bean.annotation.EzyPostInit;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import com.tvd12.ezyfox.util.EzyFileUtil;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.resources.FileUploader;

import lombok.AllArgsConstructor;

@EzySingleton
@AllArgsConstructor
public class FileUploadService {

    private final FileUploader fileUploadManager;
    private final ResourceDownloadManager resourceDownloadManager;

    @EzyPostInit
    public void postInit() {
        if (!new File("files").mkdir()) {
            System.out.println("file existed");
        }
    }

    public void accept(HttpServletRequest request) throws Exception {
        accept(
            request,
            request.getPart("file"),
            () -> System.out.println("Upload finished")
        );
    }

    public void accept(
        HttpServletRequest request,
        Part part,
        EzyExceptionVoid callback
    ) {
        String fileName = part.getSubmittedFileName();
        File file = new File("files/" + fileName);
        AsyncContext asyncContext = request.getAsyncContext();
        fileUploadManager.accept(asyncContext, part, file, callback);
    }

    public void download(
        RequestArguments requestArguments,
        String file
    ) throws Exception {
        ResourceRequestHandler handler = new ResourceRequestHandler(
            "files/" + file,
            "files/" + file,
            EzyFileUtil.getFileExtension(file),
            resourceDownloadManager
        );
        handler.handle(requestArguments);
    }
}
