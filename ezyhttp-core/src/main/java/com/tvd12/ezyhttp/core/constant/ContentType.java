package com.tvd12.ezyhttp.core.constant;

import java.util.Map;

import com.tvd12.ezyfox.util.EzyEnums;

import lombok.Getter;

@Getter
public enum ContentType {

	APPLICATION_PDF(ContentTypes.APPLICATION_PDF, "pdf"),
	APPLICATION_JSON(ContentTypes.APPLICATION_JSON, "json"),
	APPLICATION_X_WWW_FORM_URLENCODED(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED, ""),
	APPLICATION_OCTET_STREAM(ContentTypes.APPLICATION_OCTET_STREAM, ""),
	FONT_OTF(ContentTypes.FONT_OTF, "otf"),
	FONT_TTF(ContentTypes.FONT_TTF, "ttf"),
	FONT_WOFF(ContentTypes.FONT_WOFF, "woff"),
	FONT_WOFF2(ContentTypes.FONT_WOFF2, "woff2"),
	IMAGE_BMP(ContentTypes.IMAGE_BMP, "bmp"),
	IMAGE_JPEG(ContentTypes.IMAGE_JPEG, "jpeg"),
	IMAGE_JPG(ContentTypes.IMAGE_JPEG, "jpg"),
	IMAGE_GIF(ContentTypes.IMAGE_GIF, "gif"),
	IMAGE_PNG(ContentTypes.IMAGE_PNG, "png"),
	IMAGE_SVG(ContentTypes.IMAGE_SVG, "svg"),
	IMAGE_TIFF(ContentTypes.IMAGE_TIFF, "tiff"),
	IMAGE_TIF(ContentTypes.IMAGE_TIFF, "tif"),
	IMAGE_WEBP(ContentTypes.IMAGE_WEBP, "webp"),
	TEXT_CSS(ContentTypes.TEXT_CSS, "css"),
	TEXT_JAVASCRIPT(ContentTypes.TEXT_JAVASCRIPT, "js"),
	TEXT_HTML(ContentTypes.TEXT_HTML, ""),
	TEXT_HTML_UTF8(ContentTypes.TEXT_HTML_UTF8, "html"),
	TEXT_PLAIN(ContentTypes.TEXT_PLAIN, "txt");
	
	private final String extension;
	private final String value;
	
	private final static Map<String, ContentType> VALUE_BY_EXTENSION =
			EzyEnums.enumMap(ContentType.class, it -> it.extension);
	
	private ContentType(String value, String extension) {
		this.value = value;
		this.extension = extension;
	}
	
	public static ContentType ofExtension(String extension) {
		ContentType value = VALUE_BY_EXTENSION.get(extension);
		if(value == null)
			value = APPLICATION_OCTET_STREAM;
		return value;
	}
}
