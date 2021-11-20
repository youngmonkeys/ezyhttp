package com.tvd12.ezyhttp.server.core.view;

import static com.tvd12.ezyfox.util.EzyFileUtil.getFileName;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.tvd12.ezyhttp.server.core.resources.ResourceFile;
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
			Properties properties = file.resourceFile.isInJar()
			        ? fileReader.read(file.resourceFile.getRelativePath())
			        : fileReader.read(new File(file.resourceFile.getFullPath()));
			answer
			    .computeIfAbsent(file.language, k -> new Properties())
			    .putAll(properties);
			answer
			    .computeIfAbsent(file.language.toLowerCase(), k -> new Properties())
			    .putAll(properties);
		}
		return answer;
	}
	
	private List<MessagesFile> getMessagesFiles(String folderPath) {
		return listResourceFiles(folderPath)
				.stream()
				.filter(it -> it.isFileNameMatches(MESSAGES_FILE_PATTERN))
				.map(it -> {
					String fileName = getFileName(it.getRelativePath());
					int index = fileName.indexOf('_');
					String lang = "";
					if(index > 0)
						lang = fileName.substring(index + 1, fileName.lastIndexOf('.'));
					return new MessagesFile(lang, it);
				})
				.collect(Collectors.toList());
	}
	
	protected List<ResourceFile> listResourceFiles(String folderPath) {
	    return new ResourceLoader().listResourceFiles(folderPath);
	}
	
	@AllArgsConstructor
	private static final class MessagesFile {
		private final String language;
		private final ResourceFile resourceFile;
	}
	
}
