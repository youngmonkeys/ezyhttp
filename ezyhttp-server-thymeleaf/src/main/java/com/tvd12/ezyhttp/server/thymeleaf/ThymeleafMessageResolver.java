package com.tvd12.ezyhttp.server.thymeleaf;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.server.core.view.AbsentMessageResolver;
import com.tvd12.ezyhttp.server.core.view.I18nMessageResolver;
import com.tvd12.ezyhttp.server.core.view.MessageProvider;
import com.tvd12.ezyhttp.server.core.view.MessageReader;
import lombok.Getter;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.IMessageResolver;

import java.text.MessageFormat;
import java.util.*;

import static com.tvd12.ezyhttp.core.constant.Constants.EMPTY_STRING;
import static com.tvd12.ezyhttp.core.util.Locales.getLocaleFromLanguage;

public class ThymeleafMessageResolver implements
    I18nMessageResolver,
    IMessageResolver {

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
        this.messagesByLocale = mapMessagesToLocale();
        this.defaultMessages = messagesByLanguage
            .computeIfAbsent(EMPTY_STRING, it -> new Properties());
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
        MessageReader messageReader = MessageReader.getDefault();
        return messageReader.read(messageLocation);
    }

    private void mergeAnswerMessages(
        Map<String, Properties> answer,
        Map<String, Properties> messagesMap
    ) {
        for (String language : messagesMap.keySet()) {
            Properties messages = messagesMap.get(language);
            answer
                .computeIfAbsent(language, k -> new Properties())
                .putAll(messages);
            answer
                .computeIfAbsent(language.toLowerCase(), k -> new Properties())
                .putAll(messages);
        }
    }

    private Map<Locale, Properties> mapMessagesToLocale() {
        Map<Locale, Properties> messagesByLocale = new HashMap<>();
        for (String lang : messagesByLanguage.keySet()) {
            if (!lang.isEmpty()) {
                Locale locale = getLocaleFromLanguage(lang);
                messagesByLocale.put(locale, messagesByLanguage.get(lang));
            }
        }
        return messagesByLocale;
    }

    @Override
    public void putI18nMessages(
        Map<String, Map<String, String>> newMessagesByLanguage
    ) {
        for (String lang : newMessagesByLanguage.keySet()) {
            Map<String, String> messages = newMessagesByLanguage
                .get(lang);
            messagesByLanguage
                .computeIfAbsent(lang, k -> new Properties())
                .putAll(messages);
            if (lang.isEmpty()) {
                defaultMessages.putAll(messages);
            } else {
                Locale locale = getLocaleFromLanguage(lang);
                messagesByLocale
                    .computeIfAbsent(locale, k -> new Properties())
                    .putAll(messages);
            }
        }
    }

    @Override
    public String resolveMessage(
        ITemplateContext context,
        Class<?> origin,
        String key,
        Object[] parameters
    ) {
        Locale locale = context.getLocale();
        return resolveMessage(
            locale,
            key,
            parameters
        );
    }

    public String resolveMessage(
        Locale locale,
        String key,
        Object[] parameters
    ) {
        String message = null;
        Properties messages = messagesByLocale.get(locale);
        if (messages != null) {
            message = messages.getProperty(key);
        }
        if (message == null) {
            messages = messagesByLanguage.get(locale.getLanguage());
            if (messages != null) {
                message = messages.getProperty(key);
            }
        }
        if (message == null) {
            message = defaultMessages.getProperty(key);
        }
        return message != null ? formatMessage(locale, message, parameters) : null;
    }

    @Override
    public String createAbsentMessageRepresentation(
        ITemplateContext context,
        Class<?> origin,
        String key,
        Object[] parameters
    ) {
        return createAbsentMessageRepresentation(
            context.getLocale(),
            key,
            parameters
        );
    }

    public String createAbsentMessageRepresentation(
        Locale locale,
        String key,
        Object[] parameters
    ) {
        if (absentMessageResolver != null) {
            String message = absentMessageResolver.resolve(
                locale,
                getClass(),
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
        if (!isFormatCandidate(message)) {
            return message;
        }
        MessageFormat messageFormat = new MessageFormat(message, locale);
        return messageFormat.format(
            parameters != null ? parameters : EMPTY_MESSAGE_PARAMETERS
        );
    }

    private static boolean isFormatCandidate(String message) {
        char ch;
        int n = message.length();
        while ((n--) != 0) {
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
