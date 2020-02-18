package com.tvd12.ezyhttp.core.constant;

public final class ContentTypes {

	public static final String TEXT_PLAIN = "text/plain";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_HTML_UTF8 = "text/html; charset=utf-8";
	
	private ContentTypes() {}

	public static String getContentType(String contentTypeCharset) {
		String[] strs = contentTypeCharset.split(";");
		return strs[0];
	}
	
}
