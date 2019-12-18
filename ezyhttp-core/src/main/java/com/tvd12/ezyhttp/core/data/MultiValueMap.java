package com.tvd12.ezyhttp.core.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class MultiValueMap {

	protected final Map<String, List<String>> map;
	
	public MultiValueMap(Map<String, List<String>> map) {
		this.map = map;
	}
	
	public static MultiValueMap of(Map<String, List<String>> map) {
		return new MultiValueMap(map);
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
	
	public Map<String, String> toMap() {
		Map<String, String> answer = new HashMap<>();
		for(Entry<String, List<String>> entry : map.entrySet()) {
			String key = entry.getKey();
			List<String> values = entry.getValue();
			StringBuilder valueString = new StringBuilder();
			for(int i = 0 ; i < values.size() ; ++i) {
				if(i > 0) 
					valueString.append(";");
				valueString.append(values.get(i));
			}
			answer.put(key, valueString.toString());
		}
		return answer;
	}
	
}
