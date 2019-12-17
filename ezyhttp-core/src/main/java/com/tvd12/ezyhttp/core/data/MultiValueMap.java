package com.tvd12.ezyhttp.core.data;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tvd12.ezyfox.io.EzyStrings;

public class MultiValueMap {

	protected final Map<String, List<String>> map;
	
	public MultiValueMap(Map<String, List<String>> map) {
		this.map = map;
	}
	
	public Set<String> keySets() {
		return map.keySet();
	}
	
	public String getValue(String key) {
		List<String> values = map.get(key);
		if(values == null || values.isEmpty())
			return null;
		String value = values.get(0);
		return value;
	}
	
	public String getValue(String key, String defaultValue) {
		String value = getValue(key);
		return value != null ? value : defaultValue;
	}
	
	public List<String> getValues(String key) {
		List<String> values = map.get(key);
		return values;
	}
	
	@Override
	public String toString() {
		return map.toString();
	}
	
	public Map<String, String> encode() throws IOException {
		Map<String, String> encoded = new HashMap<>();
		for(Entry<String, List<String>> entry : map.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			String encodedKey = URLEncoder.encode(key, EzyStrings.UTF_8);
			StringBuilder encodedValues = new StringBuilder();
			for(int i = 0 ; i < values.size() ; ++i) {
				if(i > 0) 
					encodedValues.append(";");
				encodedValues.append(URLEncoder.encode(values.get(i), EzyStrings.UTF_8));
			}
			encoded.put(encodedKey, encodedValues.toString());
		}
		return encoded;
	}
	
	public static MultiValueMap decode(
			Map<String, List<String>> map) throws IOException {
		Map<String, List<String>> decoded = new HashMap<>();
		for(Entry<String, List<String>> entry : map.entrySet()) {
			String encodedKey = entry.getKey();
			if(encodedKey == null)
				continue;
			List<String> encodedValues = entry.getValue();
			String key = URLDecoder.decode(encodedKey, EzyStrings.UTF_8);
			List<String> values = new ArrayList<>();
			for(String encodedValue : encodedValues) {
				values.add(URLDecoder.decode(encodedValue, EzyStrings.UTF_8));
			}
			decoded.put(key, values);
		}
		return new MultiValueMap(decoded);
	}
	
}
