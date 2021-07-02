package com.tvd12.ezyhttp.server.thymeleaf;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;

import com.tvd12.ezyhttp.server.core.view.MessageReader;

public class ThymeleafMessageResolver implements IMessageResolver {
	
	private final Properties defaultMessages;
	private final Map<Locale, Properties> messagesByLocale;
	
	private static final Object[] EMPTY_MESSAGE_PARAMETERS = new Object[0];
	
	public ThymeleafMessageResolver(String folderPath) {
		MessageReader messageReader = new MessageReader();
		Map<String, Properties> messagesByLanguague = messageReader.read(folderPath);
		defaultMessages = messagesByLanguague.getOrDefault("", new Properties());
		messagesByLocale = new HashMap<>();
		for(String lang : messagesByLanguague.keySet()) {
			if(lang.length() > 0) {
				Locale locale;
				if(lang.contains("_")) {
					int index = lang.indexOf('_');
					String language = lang.substring(0, index);
					String country = lang.substring(index + 1);
					locale = new Locale(language, country);
				}
				else if(lang.contains("-")) {
					int index = lang.indexOf('-');
					String language = lang.substring(0, index);
					String country = lang.substring(index + 1);
					locale = new Locale(language, country);
				}
				else {
					locale = new Locale(lang);
				}
				messagesByLocale.put(locale, messagesByLanguague.get(lang));
			}
		}
	}

	@Override
	public String getName() {
		return "DEFAULT";
	}

	@Override
	public Integer getOrder() {
		return 0;
	}

	@Override
	public String resolveMessage(
			ITemplateContext context, 
			Class<?> origin,
			String key, 
			Object[] messageParameters) {
		Locale locale = context.getLocale();
		String message;
		Properties messages = messagesByLocale.get(locale);
		if(messages == null) {
			message = defaultMessages.getProperty(key);
		}
		else {
			message = messages.getProperty(key);
			if(message == null)
				message = defaultMessages.getProperty(key);
		}
		return message;
	}

	@Override
	public String createAbsentMessageRepresentation(
			ITemplateContext context, 
			Class<?> origin, 
			String key,
			Object[] messageParameters) {
		return key;
	}

	protected String formatMessage(final Locale locale, final String message, final Object[] messageParameters) {
		if (message == null) {
			return null;
		}
		if (!isFormatCandidate(message)) {
			return message;
		}
		final MessageFormat messageFormat = new MessageFormat(message, locale);
		return messageFormat.format((messageParameters != null ? messageParameters : EMPTY_MESSAGE_PARAMETERS));
	}

	private static boolean isFormatCandidate(final String message) {
		char c;
		int n = message.length();
		while (n-- != 0) {
			c = message.charAt(n);
			if (c == '}' || c == '\'') {
				return true;
			}
		}
		return false;
	}

}
