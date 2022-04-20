package com.tvd12.ezyhttp.server.core.request;

import com.tvd12.ezyfox.builder.EzyBuilder;

import lombok.Getter;

@Getter
@SuppressWarnings("AbbreviationAsWordInName")
public class RequestURIMeta {

    private final boolean api;
    private final boolean authenticated;
    private final boolean management;
    private final boolean resource;
    private final boolean payment;
    private final String feature;
    private final String resourceFullPath;

    protected RequestURIMeta(Builder builder) {
        this.api = builder.api;
        this.authenticated = builder.authenticated;
        this.management = builder.management;
        this.resource = builder.resource;
        this.payment = builder.payment;
        this.feature = builder.feature;
        this.resourceFullPath = builder.resourceFullPath;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<RequestURIMeta> {

        private boolean api;
        private boolean authenticated;
        private boolean management;
        private boolean resource;
        private boolean payment;
        private String feature;
        private String resourceFullPath;

        public Builder api(boolean api) {
            this.api = api;
            return this;
        }

        public Builder authenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public Builder management(boolean management) {
            this.management = management;
            return this;
        }

        public Builder resource(boolean resource) {
            this.resource = resource;
            return this;
        }

        public Builder payment(boolean payment) {
            this.payment = payment;
            return this;
        }

        public Builder feature(String feature) {
            this.feature = feature;
            return this;
        }

        public Builder resourceFullPath(String resourceFullPath) {
            this.resourceFullPath = resourceFullPath;
            return this;
        }

        @Override
        public RequestURIMeta build() {
            return new RequestURIMeta(this);
        }
    }
}
