package com.tvd12.ezyhttp.core.constant;

import com.tvd12.ezyfox.util.EzyEnums;
import lombok.Getter;

import java.util.Map;

@Getter
public enum ContentType {

    AAC_AUDIO(ContentTypes.AAC_AUDIO, "aac"),
    ABI_WORD_DOCUMENT(ContentTypes.ABI_WORD_DOCUMENT, "abw"),
    ARCHIVE_DOCUMENT(ContentTypes.ARCHIVE_DOCUMENT, "arc"),
    AUDIO_VIDEO_INTERLEAVE(ContentTypes.AUDIO_VIDEO_INTERLEAVE, "avi"),
    AMAZON_KINDLE_EBOOK(ContentTypes.AMAZONE_KINDLE_EBOOK, "azw"),
    BZIP(ContentTypes.BZIP, "bz"),
    BZIP2(ContentTypes.BZIP2, "bz2"),
    CD_AUDIO(ContentTypes.CD_AUDIO, "cda"),
    CSHELL_SCRIPT(ContentTypes.CSHELL_SCRIPT, "csh"),
    TEXT_CSV(ContentTypes.TEXT_CSV, "csv"),
    MICROSOFT_WORD(ContentTypes.MICROSOFT_WORD, "doc"),
    MICROSOFT_WORD_X(ContentTypes.MICROSOFT_WORD_X, "docx"),
    MS_EMBEDDED_OPENTYPE_FONTS(ContentTypes.MS_EMBEDDED_OPENTYPE_FONTS, "eot"),
    EPUB(ContentTypes.EPUB, "epub"),
    GZIP(ContentTypes.GZIP, "gz"),
    ICON(ContentTypes.ICON, "ico"),
    ICALENDAR(ContentTypes.ICALENDAR, "ics"),
    JAR(ContentTypes.JAR, "jar"),
    JSONLD(ContentTypes.JSONLD, "jsonld"),
    MUSICAL_INSTRUMENT_DIGITAL_INTERFACE(
        ContentTypes.MUSICAL_INSTRUMENT_DIGITAL_INTERFACE,
        "mid"
    ),
    MUSICAL_INSTRUMENT_DIGITAL_INTERFACE_X(
        ContentTypes.MUSICAL_INSTRUMENT_DIGITAL_INTERFACE_X,
        "midi"
    ),
    TEXT_JAVASCRIPT_MODULE(ContentTypes.TEXT_JAVASCRIPT, "mjs"),
    MP3(ContentTypes.MP3, "mp3"),
    MP4(ContentTypes.MP4, "mp4"),
    MPGEG(ContentTypes.MPGEG, "mpeg"),
    APPLE_INSTALLER_PACKAGE(ContentTypes.APPLE_INSTALLER_PACKAGE, "mpkg"),
    OPEN_DOCUMENT_PRESENTATION(ContentTypes.OPEN_DOCUMENT_PRESENTATION, "odp"),
    OPENDOCUMENT_SPREADSHEET(ContentTypes.OPENDOCUMENT_SPREADSHEET, "ods"),
    OPENDOCUMENT_TEXT(ContentTypes.OPENDOCUMENT_TEXT, "odt"),
    OGG_AUDIO(ContentTypes.OGG_AUDIO, "oga"),
    OGG_VIDEO(ContentTypes.OGG_VIDEO, "ogv"),
    OGG(ContentTypes.OGG, "ogx"),
    OPUS_AUDIO(ContentTypes.OPUS_AUDIO, "opus"),
    PHP(ContentTypes.PHP, "php"),
    MICROSOFT_POWERPOINT(ContentTypes.MICROSOFT_POWERPOINT, "ppt"),
    MICROSOFT_POWERPOINT_X(ContentTypes.MICROSOFT_POWERPOINT_X, "pptx"),
    RAR(ContentTypes.RAR, "rar"),
    RICH_TEXT_FORMAT(ContentTypes.RICH_TEXT_FORMAT, "rtf"),
    BOURNE_SHELL_SCRIPT(ContentTypes.BOURNE_SHELL_SCRIPT, "sh"),
    ADOBE_SMALL_WEB_FORMAT(ContentTypes.ADOBE_SMALL_WEB_FORMAT, "swf"),
    TAR(ContentTypes.TAR, "tar"),
    MPEG_TRANSPORT_STREAM(ContentTypes.MPEG_TRANSPORT_STREAM, "ts"),
    MICROSOFT_VISIO(ContentTypes.MICROSOFT_VISIO, "vsd"),
    WAV(ContentTypes.WAV, "wav"),
    WEBM_AUDIO(ContentTypes.WEBM_AUDIO, "weba"),
    WEBM_VIDEO(ContentTypes.WEBM_VIDEO, "webm"),
    XHTML(ContentTypes.XHTML, "xhtml"),
    MICROSOFT_EXCEL(ContentTypes.MICROSOFT_EXCEL, "xls"),
    MICROSOFT_EXCEL_X(ContentTypes.MICROSOFT_EXCEL_X, "xlsx"),
    APPLICATION_XML(ContentTypes.APPLICATION_XML, "xml"),
    APPLICATION_XUL(ContentTypes.APPLICATION_XUL, "xul"),
    ZIP(ContentTypes.ZIP, "zip"),
    THREE_GP_VIDEO(ContentTypes.THREE_GP_VIDEO, "3gp"),
    THREE_GP_AUDIO(ContentTypes.THREE_GP_AUDIO, "3gp"),
    THREE_GP_VIDEO_2(ContentTypes.THREE_GP_VIDEO_2, "3g2"),
    THREE_GP_AUDIO_2(ContentTypes.THREE_GP_AUDIO_2, "3g2"),
    SEVEN_ZIP(ContentTypes.SEVEN_ZIP, "7z"),
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

    private static final Map<String, ContentType> VALUE_BY_MIME_TYPE =
        EzyEnums.enumMap(ContentType.class, it -> it.value);

    private static final Map<String, ContentType> VALUE_BY_EXTENSION =
        EzyEnums.enumMap(ContentType.class, it -> it.extension);

    ContentType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public String getMimeType() {
        return value;
    }

    public static ContentType ofMimeType(String mimeType) {
        return VALUE_BY_MIME_TYPE.get(mimeType);
    }

    public static ContentType ofExtension(String extension) {
        return VALUE_BY_EXTENSION.getOrDefault(
            extension,
            APPLICATION_OCTET_STREAM
        );
    }

    public static String getExtensionOfMimeType(
        String mimeType,
        String defaultExtension
    ) {
        ContentType contentType = VALUE_BY_MIME_TYPE.get(mimeType);
        if (contentType != null) {
            return contentType.getExtension();
        }
        return defaultExtension;
    }
}
