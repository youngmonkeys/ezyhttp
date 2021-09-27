package com.tvd12.ezyhttp.server.core.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.tvd12.ezyhttp.server.core.resources.ResourceLoader;
import com.tvd12.properties.file.reader.BaseFileReader;
import com.tvd12.properties.file.reader.FileReader;

import lombok.AllArgsConstructor;

public class MessageReader {
	
	private static final String MESSAGES_FILE_PATTERN = "^messages(|[_\\-\\w\\d]+).properties";

	public Map<String, Properties> read(String folderPath) {
		List<MessagesFile> files = getMessagesFiles(folderPath);
		Map<String, Properties> answer = new HashMap<>();
		FileReader fileReader = new BaseFileReader();
		for(MessagesFile file : files) {
			Properties properties = fileReader.read(file.filePath);
			answer.put(file.languague, properties);
			answer.put(file.languague.toLowerCase(), properties);
		}
		return answer;
	}
	
	private List<MessagesFile> getMessagesFiles(String folderPath) {
		return new ResourceLoader().listResources(folderPath)
				.stream()
				.filter(it -> getFileName(it).matches(MESSAGES_FILE_PATTERN))
				.map(it -> {
					String fileName = getFileName(it);
					int index = fileName.indexOf('_');
					String lang = "";
					if(index > 0)
						lang = fileName.substring(index + 1, fileName.lastIndexOf('.'));
					return new MessagesFile(lang, it);
				})
				.collect(Collectors.toList());
	}
	
	private String getFileName(String filePath) {
		int index = filePath.lastIndexOf('/');
		if(index < 0)
			return filePath;
		if(index >= filePath.length() - 1)
			return filePath;
		return filePath.substring(index + 1, filePath.length());
	}
	
	@AllArgsConstructor
	private static final class MessagesFile {
		private final String languague;
		private final String filePath;
	}
	
}
