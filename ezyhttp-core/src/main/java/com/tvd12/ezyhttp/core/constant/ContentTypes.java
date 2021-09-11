package com.tvd12.ezyhttp.core.constant;

public final class ContentTypes {

	public static final String APPLICATION_PDF = "application/pdf";
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
	public static final String FONT_OTF = "font/otf";
	public static final String FONT_TTF = "font/ttf";
	public static final String FONT_WOFF = "font/woff";
	public static final String FONT_WOFF2 = "font/woff2";
	public static final String IMAGE_BMP = "image/bmp";
	public static final String IMAGE_JPEG = "image/jpeg";
	public static final String IMAGE_GIF = "image/gif";
	public static final String IMAGE_PNG = "image/png";
	public static final String IMAGE_SVG = "image/svg+xml";
	public static final String IMAGE_TIFF = "image/tiff";
	public static final String IMAGE_WEBP = "image/webp";
	public static final String MULTIPART_FORM_DATA = "multipart/form-data";
	public static final String TEXT_CSS = "text/css";
	public static final String TEXT_JAVASCRIPT = "text/javascript";
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_HTML_UTF8 = "text/html; charset=utf-8";
	public static final String TEXT_PLAIN = "text/plain";
	
	private ContentTypes() {}

	public static String getContentType(String contentTypeCharset) {
		if(contentTypeCharset == null)
			return null;
		int index = contentTypeCharset.indexOf(';');
		return index > 0 ? contentTypeCharset.substring(0, index) : contentTypeCharset;
	}
	
}
