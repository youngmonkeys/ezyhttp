package com.tvd12.ezyhttp.server.thymeleaf;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.server.core.view.AbsentMessageResolver;
import com.tvd12.ezyhttp.server.core.view.MessageProvider;
import com.tvd12.ezyhttp.server.core.view.MessageReader;

import lombok.Getter;

public class ThymeleafMessageResolver implements IMessageResolver {
    
    @Getter
    private final String name = NAME;
    @Getter
    private final Integer order = ORDER;
    private final String messageLocation;
    private final Properties defaultMessages;
    private final Map<Locale, Properties> messagesByLocale;
    private final Map<String, Properties> messagesByLanguage;
    private final List<MessageProvider> messageProviders;
    private final AbsentMessageResolver absentMessageResolver;

    private static final int ORDER = 0;
    private static final String NAME = "DEFAULT";
    private static final Object[] EMPTY_MESSAGE_PARAMETERS = new Object[0];
    
    protected ThymeleafMessageResolver(Builder builder) {
        this.messageLocation = builder.messageLocation;
        this.messageProviders = builder.messageProviders;
        this.absentMessageResolver = builder.absentMessageResolver;
        this.messagesByLanguage = collectMessages();
        this.messagesByLocale = mapMessagesToLocal();
        this.defaultMessages = messagesByLanguage.computeIfAbsent("", it -> new Properties());
    }
    
    private Map<String, Properties> collectMessages() {
        Map<String, Properties> answer = new HashMap<>();
        mergeAnswerMessages(answer, readMessages());
        for (MessageProvider provider : messageProviders) {
            mergeAnswerMessages(answer, provider.provide());
        }
        return answer;
    }
    
    private Map<String, Properties> readMessages() {
        MessageReader messageReader = new MessageReader();
        return messageReader.read(messageLocation);
    }
    
    private void mergeAnswerMessages(
        Map<String, Properties> answer,
        Map<String, Properties> messagesMap
    ) {
        for (String language : messagesMap.keySet()) {
            Properties messages = messagesMap.get(language);
            answer
                .computeIfAbsent(language, (k) -> new Properties())
                .putAll(messages);
            answer
                .computeIfAbsent(language.toLowerCase(), k -> new Properties())
                .putAll(messages);
        }
    }
    
    private Map<Locale, Properties> mapMessagesToLocal() {
        Map<Locale, Properties> messagesByLocale = new HashMap<>();
        for(String lang : messagesByLanguage.keySet()) {
            if (lang.length() > 0) {
                Locale locale;
                if (lang.contains("_")) {
                    int index = lang.indexOf('_');
                    String language = lang.substring(0, index);
                    String country = lang.substring(index + 1);
                    locale = new Locale(language, country);
                }
                else if (lang.contains("-")) {
                    int index = lang.indexOf('-');
                    String language = lang.substring(0, index);
                    String country = lang.substring(index + 1);
                    locale = new Locale(language, country);
                }
                else {
                    locale = new Locale(lang);
                }
                messagesByLocale.put(locale, messagesByLanguage.get(lang));
            }
        }
        return messagesByLocale;
    }

    @Override
    public String resolveMessage(
            ITemplateContext context, 
            Class<?> origin,
            String key, 
            Object[] parameters) {
        Locale locale = context.getLocale();
        String message;
        Properties messages = messagesByLocale.get(locale);
        if (messages == null) {
            messages = messagesByLanguage.get(locale.getLanguage());
        }
        if (messages == null) {
            message = defaultMessages.getProperty(key);
        }
        else {
            message = messages.getProperty(key);
            if (message == null) {
                message = defaultMessages.getProperty(key);
            }
        }
        return message != null ? formatMessage(locale, message, parameters) : null;
    }

    @Override
    public String createAbsentMessageRepresentation(
            ITemplateContext context, 
            Class<?> origin, 
            String key,
            Object[] parameters) {
        if (absentMessageResolver != null) {
            String message = absentMessageResolver.resolve(
                context.getLocale(), 
                origin, 
                key, 
                parameters
            );
            if (message != null) {
                return message;
            }
        }
        return key;
    }

    private String formatMessage(Locale locale, String message, Object[] parameters) {
        if (!isFormatCandidate(message))
            return message;
        MessageFormat messageFormat = new MessageFormat(message, locale);
        return messageFormat.format(
            parameters != null ? parameters : EMPTY_MESSAGE_PARAMETERS
        );
    }

    private static boolean isFormatCandidate(String message) {
        char ch;
        int n = message.length();
        while ((n --) != 0) {
            ch = message.charAt(n);
            if (ch == '}' || ch == '\'') {
                return true;
            }
        }
        return false;
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<ThymeleafMessageResolver> {
        
        private String messageLocation;
        private List<MessageProvider> messageProviders;
        private AbsentMessageResolver absentMessageResolver;
        
        public Builder messageLocation(String messageLocation) {
            this.messageLocation = messageLocation;
            return this;
        }
        
        public Builder messageProviders(List<MessageProvider> messageProviders) {
            this.messageProviders = messageProviders;
            return this;
        }
        
        public Builder absentMessageResolver(AbsentMessageResolver absentMessageResolver) {
            this.absentMessageResolver = absentMessageResolver;
            return this;
        }
        
        @Override
        public ThymeleafMessageResolver build() {
            return new ThymeleafMessageResolver(this);
        }
    }
}
