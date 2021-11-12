package com.tvd12.ezyhttp.core.boot.test.service;

import java.io.File;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.tvd12.ezyfox.bean.annotation.EzyPostInit;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import com.tvd12.ezyhttp.server.core.resources.FileUploader;

import lombok.AllArgsConstructor;

@EzySingleton
@AllArgsConstructor
public class FileUploadService {

	private final FileUploader fileUploadManager;
	
	@EzyPostInit
	public void postInit() {
		new File("files").mkdir();
	}
	
	public void accept(HttpServletRequest request, Part part, EzyExceptionVoid callback) {
		String fileName = part.getSubmittedFileName();
		File file = new File("files/" + fileName);
		AsyncContext asyncContext = request.getAsyncContext();
		fileUploadManager.accept(asyncContext, part, file, callback);
	}
	
}
