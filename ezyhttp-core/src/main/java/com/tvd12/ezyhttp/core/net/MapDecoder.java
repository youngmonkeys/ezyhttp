package com.tvd12.ezyhttp.core.net;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.tvd12.ezyfox.io.EzyStrings;

public final class MapDecoder {

	private MapDecoder() {}
	
	public static Map<String, String> decodeFromBytes(byte[] bytes) throws IOException {
		String string = new String(bytes, StandardCharsets.UTF_8);
        return decodeFromString(string);
	}
	
	public static Map<String, String> decodeFromString(String string) throws IOException {
		Map<String, String> map = new HashMap<>();
		if (EzyStrings.isNoContent(string))
			return map;
		String[] keyValues = string.split("&");
		for(String keyValue : keyValues) {
			String[] kv = keyValue.split("=");
			String key = URLDecoder.decode(kv[0], EzyStrings.UTF_8);
			String value = URLDecoder.decode(kv[1], EzyStrings.UTF_8);
			map.put(key, value);
		}
		return map;
	}
	
}
