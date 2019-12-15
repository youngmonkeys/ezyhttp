package com.tvd12.ezyhttp.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface BodyData {

	Map<String, String> getParameters();
	
	String getContentType();
	
	int getContentLength();
	
	InputStream getInputStream() throws IOException;
	
}
