package com.tvd12.ezyhttp.core.net;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import com.tvd12.ezyfox.io.EzyStrings;

public final class MapEncoder {

    private MapEncoder() {}

    public static byte[] encodeToBytes(Map<String, Object> map) throws IOException {
        String str = encodeToString(map);
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String encodeToString(Map<String, Object> map) throws IOException {
        boolean first = true;
        StringBuilder builder = new StringBuilder();
        for (Entry<String, Object> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey(), EzyStrings.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), EzyStrings.UTF_8));
        }
        return builder.toString();
    }
}
