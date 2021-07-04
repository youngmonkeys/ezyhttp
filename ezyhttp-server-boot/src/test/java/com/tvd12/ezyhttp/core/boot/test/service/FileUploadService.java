package com.tvd12.ezyhttp.core.boot.test.service;

import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.Part;

import com.tvd12.ezyfox.bean.annotation.EzyPostInit;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;
import com.tvd12.ezyhttp.server.core.resources.ResourceUploadManager;

import lombok.AllArgsConstructor;

@EzySingleton
@AllArgsConstructor
public class FileUploadService {

	private final ResourceUploadManager resourceUploadManager;
	
	@EzyPostInit
	public void postInit() {
		new File("files").mkdir();
	}
	
	public void accept(Part part) throws Exception {
		String fileName = part.getSubmittedFileName();
		FileOutputStream outputStream = new FileOutputStream(new File("files/" + fileName));
		try {
			resourceUploadManager.drain(part.getInputStream(), outputStream);
		}
		finally {
			outputStream.close();
		}
	}
	
}
