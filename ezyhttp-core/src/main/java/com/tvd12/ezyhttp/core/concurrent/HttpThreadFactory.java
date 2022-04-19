package com.tvd12.ezyhttp.core.concurrent;

import com.tvd12.ezyfox.concurrent.EzyThreadFactory;

public class HttpThreadFactory extends EzyThreadFactory {
    
    protected HttpThreadFactory(Builder builder) {
        super(builder);
    }
    
    public static HttpThreadFactory create(String poolName) {
        return (HttpThreadFactory) builder()
                .poolName(poolName)
                .build();
    }
    
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends EzyThreadFactory.Builder {
        
        protected Builder() {
            super();
            this.prefix = "ezyhttp";
        }
        
        @Override
        public HttpThreadFactory build() {
            return new HttpThreadFactory(this);
        }
        
    }
    
}
