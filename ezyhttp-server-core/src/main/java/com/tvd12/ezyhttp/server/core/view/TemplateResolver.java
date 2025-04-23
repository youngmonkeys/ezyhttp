package com.tvd12.ezyhttp.server.core.view;

import com.tvd12.ezyfox.bean.EzyPropertyFetcher;
import com.tvd12.ezyfox.builder.EzyBuilder;
import lombok.Getter;

import static com.tvd12.ezyhttp.server.core.constant.PropertyNames.*;

@Getter
public class TemplateResolver {

    private final String prefix;
    private final String suffix;
    private final String characterEncoding;
    private final int cacheTTLMs;
    private final boolean cacheable;
    private final String templateMode;
    private final String messagesLocation;

    protected TemplateResolver(Builder builder) {
        this.prefix = builder.prefix;
        this.suffix = builder.suffix;
        this.characterEncoding = builder.characterEncoding;
        this.cacheTTLMs = builder.cacheTTLMs;
        this.cacheable = builder.cacheable;
        this.templateMode = builder.templateMode;
        this.messagesLocation = builder.messagesLocation;
    }

    public static TemplateResolver of(EzyPropertyFetcher propertyFetcher) {
        return builder().setFrom(propertyFetcher).build();
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder implements EzyBuilder<TemplateResolver> {
        private String prefix = "templates/";
        private String suffix = ".html";
        private String characterEncoding = "UTF-8";
        private int cacheTTLMs = 3600000;
        private boolean cacheable = true;
        private String templateMode = "HTML";
        private String messagesLocation = "messages";

        public Builder prefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public Builder suffix(String suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder characterEncoding(String characterEncoding) {
            this.characterEncoding = characterEncoding;
            return this;
        }

        public Builder cacheTTLMs(int cacheTTLMs) {
            this.cacheTTLMs = cacheTTLMs;
            return this;
        }

        public Builder cacheable(boolean cacheable) {
            this.cacheable = cacheable;
            return this;
        }

        public Builder templateMode(String templateMode) {
            this.templateMode = templateMode;
            return this;
        }

        public Builder messagesLocation(String messagesLocation) {
            this.messagesLocation = messagesLocation;
            return this;
        }

        public Builder setFrom(EzyPropertyFetcher propertyFetcher) {
            this.templateMode = propertyFetcher.getProperty(
                VIEW_TEMPLATE_MODE,
                String.class,
                templateMode
            );
            this.prefix = propertyFetcher.getProperty(
                VIEW_TEMPLATE_PREFIX,
                String.class,
                prefix
            );
            this.suffix = propertyFetcher.getProperty(
                VIEW_TEMPLATE_SUFFIX,
                String.class,
                suffix
            );
            this.characterEncoding = propertyFetcher.getProperty(
                VIEW_TEMPLATE_CHARACTER_ENCODING,
                String.class,
                characterEncoding
            );
            this.cacheTTLMs = propertyFetcher.getProperty(
                VIEW_TEMPLATE_CACHE_TTL_MS,
                int.class,
                cacheTTLMs
            );
            this.cacheable = propertyFetcher.getProperty(
                VIEW_TEMPLATE_CACHEABLE,
                boolean.class,
                cacheable
            );
            this.messagesLocation = propertyFetcher.getProperty(
                VIEW_TEMPLATE_MESSAGES_LOCATION,
                String.class,
                messagesLocation
            );
            return this;
        }

        @Override
        public TemplateResolver build() {
            return new TemplateResolver(this);
        }
    }
}
