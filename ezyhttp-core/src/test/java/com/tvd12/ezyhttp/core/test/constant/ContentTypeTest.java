package com.tvd12.ezyhttp.core.test.constant;

import com.tvd12.ezyhttp.core.constant.ContentType;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

public class ContentTypeTest {

    @Test
    public void ofExtensionTest() {
        Asserts.assertEquals(ContentType.ofExtension("pdf"), ContentType.APPLICATION_PDF);
        Asserts.assertEquals(ContentType.ofExtension("json"), ContentType.APPLICATION_JSON);
        Asserts.assertEquals(ContentType.ofExtension("otf"), ContentType.FONT_OTF);
        Asserts.assertEquals(ContentType.ofExtension("ttf"), ContentType.FONT_TTF);
        Asserts.assertEquals(ContentType.ofExtension("woff"), ContentType.FONT_WOFF);
        Asserts.assertEquals(ContentType.ofExtension("woff2"), ContentType.FONT_WOFF2);
        Asserts.assertEquals(ContentType.ofExtension("bmp"), ContentType.IMAGE_BMP);
        Asserts.assertEquals(ContentType.ofExtension("jpeg"), ContentType.IMAGE_JPEG);
        Asserts.assertEquals(ContentType.ofExtension("jpg"), ContentType.IMAGE_JPG);
        Asserts.assertEquals(ContentType.ofExtension("gif"), ContentType.IMAGE_GIF);
        Asserts.assertEquals(ContentType.ofExtension("png"), ContentType.IMAGE_PNG);
        Asserts.assertEquals(ContentType.ofExtension("svg"), ContentType.IMAGE_SVG);
        Asserts.assertEquals(ContentType.ofExtension("tiff"), ContentType.IMAGE_TIFF);
        Asserts.assertEquals(ContentType.ofExtension("tif"), ContentType.IMAGE_TIF);
        Asserts.assertEquals(ContentType.ofExtension("webp"), ContentType.IMAGE_WEBP);
        Asserts.assertEquals(ContentType.ofExtension("css"), ContentType.TEXT_CSS);
        Asserts.assertEquals(ContentType.ofExtension("js"), ContentType.TEXT_JAVASCRIPT);
        Asserts.assertEquals(ContentType.ofExtension("html"), ContentType.TEXT_HTML_UTF8);
        Asserts.assertEquals(ContentType.ofExtension("txt"), ContentType.TEXT_PLAIN);
        Asserts.assertEquals(ContentType.ofExtension(null), ContentType.APPLICATION_OCTET_STREAM);

        Asserts.assertEquals(ContentType.ofMimeType(ContentTypes.MP3), ContentType.MP3);
        Asserts.assertNull(ContentType.ofMimeType("not found"));
        Asserts.assertEquals(
            ContentType.MP3.getValue(),
            ContentType.MP3.getMimeType()
        );
    }

    @Test
    public void commonTest() {
        Asserts.assertEquals("json", ContentType.APPLICATION_JSON.getExtension());
        Asserts.assertEquals(ContentTypes.APPLICATION_JSON, ContentType.APPLICATION_JSON.getValue());
    }

    @Test
    public void getExtensionOfMimeTypeTest() {
        // given
        String defaultExtension = "mp3";

        // when
        String actual = ContentType.getExtensionOfMimeType(
            ContentTypes.MP3,
            defaultExtension
        );

        // then
        Asserts.assertEquals(actual, ContentType.MP3.getExtension());
    }

    @Test
    public void getExtensionOfMimeTypeButDefaultTest() {
        // given
        String defaultExtension = "mp3";

        // when
        String actual = ContentType.getExtensionOfMimeType(
            "not found",
            defaultExtension
        );

        // then
        Asserts.assertEquals(actual, defaultExtension);
    }
}
