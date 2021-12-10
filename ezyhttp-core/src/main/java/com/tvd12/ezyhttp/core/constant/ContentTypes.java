package com.tvd12.ezyhttp.core.constant;

public final class ContentTypes {

    public static final String AAC_AUDIO = "audio/aac";
    public static final String ABI_WORD_DOCUMENT = "application/x-abiword";
    public static final String ARCHIVE_DOCUMENT = "application/x-freearc";
    public static final String AUDIO_VIDEO_INTERLEAVE = "video/x-msvideo";
    public static final String AMAZONE_KINDLE_EBOOK = "application/vnd.amazon.ebook";
    public static final String BZIP = "application/x-bzip";
    public static final String BZIP2 = "application/x-bzip2";
    public static final String CD_AUDIO = "application/x-cdf";
    public static final String CSHELL_SCRIPT = "application/x-csh";
    public static final String MICROSOFT_WORD = "application/msword";
    public static final String MICROSOFT_WORD_X = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MS_EMBEDDED_OPENTYPE_FONTS = "application/vnd.ms-fontobject";
    public static final String EPUB = "application/epub+zip";
    public static final String GZIP = "application/gzip";
    public static final String ICON = "image/vnd.microsoft.icon";
    public static final String ICALENDAR = "text/calendar";
    public static final String JAR = "application/java-archive";
    public static final String JSONLD = "application/ld+json";
    public static final String MUSICAL_INSTRUMENT_DIGITAL_INTERFACE = "audio/midi";
    public static final String MUSICAL_INSTRUMENT_DIGITAL_INTERFACE_X = "audio/x-midi";
    public static final String MP3 = "audio/mpeg";
    public static final String MP4 = "video/mp4";
    public static final String MPGEG = "video/mpeg";
    public static final String APPLE_INSTALLER_PACKAGE = "application/vnd.apple.installer+xml";
    public static final String OPEN_DOCUMENT_PRESENTATION = "application/vnd.oasis.opendocument.presentation";
    public static final String OPENDOCUMENT_SPREADSHEET = "application/vnd.oasis.opendocument.spreadsheet";
    public static final String OPENDOCUMENT_TEXT = "application/vnd.oasis.opendocument.text";
    public static final String OGG_AUDIO = "audio/ogg";
    public static final String OGG_VIDEO = "video/ogg";
    public static final String OGG = "application/ogg";
    public static final String OPUS_AUDIO = "audio/opus";
    public static final String PHP = "application/x-httpd-php";
    public static final String MICROSOFT_POWERPOINT = "application/vnd.ms-powerpoint";
    public static final String MICROSOFT_POWERPOINT_X = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    public static final String RAR = "application/vnd.rar";
    public static final String RICH_TEXT_FORMAT = "application/rtf";
    public static final String BOURNE_SHELL_SCRIPT = "application/x-sh";
    public static final String ADOBE_SMALL_WEB_FORMAT = "application/x-shockwave-flash";
    public static final String TAR = "application/x-tar";
    public static final String MPEG_TRANSPORT_STREAM = "video/mp2t";
    public static final String MICROSOFT_VISIO = "application/vnd.visio";
    public static final String WAV = "audio/wav";
    public static final String WEBM_AUDIO = "audio/webm";
    public static final String WEBM_VIDEO = "video/webm";
    public static final String XHTML = "application/xhtml+xml";
    public static final String MICROSOFT_EXCEL = "application/vnd.ms-excel";
    public static final String MICROSOFT_EXCEL_X = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String APPLICATION_XML = "application/xml";
    public static final String APPLICATION_XUL = "application/vnd.mozilla.xul+xml";
    public static final String ZIP = "application/zip";
    public static final String THREE_GP_VIDEO = "video/3gpp";
    public static final String THREE_GP_AUDIO = "audio/3gpp";
    public static final String THREE_GP_VIDEO_2 = "video/3gpp2";
    public static final String THREE_GP_AUDIO_2 = "audio/3gpp2";
    public static final String SEVEN_ZIP = "application/x-7z-compressed";
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
	public static final String TEXT_CSV = "text/csv";
	
	private ContentTypes() {}

	public static String getContentType(String contentTypeCharset) {
		if(contentTypeCharset == null)
			return null;
		int index = contentTypeCharset.indexOf(';');
		return index > 0 ? contentTypeCharset.substring(0, index) : contentTypeCharset;
	}
	
}
