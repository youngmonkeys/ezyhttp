package com.tvd12.ezyhttp.server.core.util;

import java.io.IOException;
import java.io.InputStream;

import com.tvd12.ezyfox.stream.EzyClassPathInputStreamLoader;
import com.tvd12.ezyfox.stream.EzyInputStreams;
import com.tvd12.ezyfox.util.EzyProcessor;

public class BannerPrinter {

    public String getBannerText() {
        return new String(getBannerBytes());
    }

    protected byte[] getBannerBytes() {
        InputStream inputStream = getBannerInputStream();
        try {
            return getBannerBytes(inputStream);
        } finally {
            EzyProcessor.processSilently(inputStream::close);
        }
    }

    protected byte[] getBannerBytes(InputStream stream) {
        try {
            return EzyInputStreams.toByteArray(stream);
        } catch (IOException e) {
            return new byte[0];
        }
    }

    protected InputStream getBannerInputStream() {
        return EzyClassPathInputStreamLoader.builder()
            .context(getClass())
            .build()
            .load("ezyhttp-banner.txt");
    }
}
