package com.tvd12.ezyhttp.server.core.view;

import com.tvd12.ezyfox.builder.EzyBuilder;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.Cookie;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

@Getter
public class View {

    @Setter
    private Locale locale;
    @Setter
    private String template;
    @Setter
    private String contentType;
    private final List<Cookie> cookies;
    private final Map<String, String> headers;
    private final Map<String, Object> variables;

    protected View(Builder builder) {
        this.template = builder.template;
        this.locale = builder.locale;
        this.variables = builder.variables;
        this.contentType = builder.contentType;
        this.cookies = builder.cookies;
        this.headers = builder.headers;
    }

    public static View of(String template) {
        return View.builder().template(template).build();
    }

    public boolean containsVariable(String name) {
        Object value = variables.get(name);
        return value != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getVariable(String name) {
        return (T) variables.get(name);
    }

    public void setVariable(String name, Object value) {
        this.variables.put(name, value);
    }

    public void setVariableIfAbsent(String name, Object value) {
        this.variables.putIfAbsent(name, value);
    }

    public void setVariableIfAbsent(
        String name,
        Supplier<Object> valueSupplier
    ) {
        this.variables.computeIfAbsent(
            name,
            k -> valueSupplier.get()
        );
    }

    public void setVariables(Map<String, Object> variables) {
        if (variables != null) {
            this.variables.putAll(variables);
        }
    }

    @SuppressWarnings("unchecked")
    public static void appendToVariable(
        Map<String, Object> variables,
        String variableName,
        Object value
    ) {
        if (value != null) {
            ((List<Object>) variables.computeIfAbsent(
                variableName,
                k -> new ArrayList<>())
            ).add(value);
        }
    }

    public void appendToVariable(String name, Object value) {
        appendToVariable(variables, name, value);
    }

    public void appendValueToVariable(String name, Object value) {
        appendToVariable(variables, name, value);
    }

    public void appendValuesToVariable(String name, Object[] values) {
        for (Object value : values) {
            appendToVariable(variables, name, value);
        }
    }

    @SuppressWarnings("rawtypes")
    public void appendValuesToVariable(String name, Collection values) {
        for (Object value : values) {
            appendToVariable(variables, name, value);
        }
    }

    public void putKeyValueToVariable(
        String variableName,
        String key,
        Object value
    ) {
        View.putKeyValueToVariable(variables, variableName, key, value);
    }

    @SuppressWarnings("unchecked")
    public static void putKeyValueToVariable(
        Map<String, Object> variables,
        String variableName,
        String key,
        Object value
    ) {
        if (key != null && value != null) {
            ((Map<String, Object>) variables.computeIfAbsent(
                variableName,
                k -> new HashMap<>())
            ).put(key, value);
        }
    }

    public void putKeyValuesToVariable(
        String variableName,
        Map<String, Object> keyValues
    ) {
        for (Map.Entry<String, Object> e : keyValues.entrySet()) {
            putKeyValueToVariable(variableName, e.getKey(), e.getValue());
        }
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements EzyBuilder<View> {
        private String template;
        private List<Cookie> cookies;
        private Map<String, String> headers;
        private Locale locale = Locale.ENGLISH;
        private String contentType = ContentTypes.TEXT_HTML_UTF8;
        private final Map<String, Object> variables = new HashMap<>();

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder locale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder locale(String locale) {
            return locale(new Locale(locale));
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder addVariable(String name, Object value) {
            this.variables.put(name, value);
            return this;
        }

        public Builder addVariables(Map<String, Object> variables) {
            if (variables != null) {
                this.variables.putAll(variables);
            }
            return this;
        }

        public Builder appendToVariable(String variableName, Object value) {
            View.appendToVariable(variables, variableName, value);
            return this;
        }

        public Builder appendValueToVariable(String variableName, Object value) {
            View.appendToVariable(variables, variableName, value);
            return this;
        }

        public Builder appendValuesToVariable(
            String variableName,
            Object[] values
        ) {
            for (Object value : values) {
                View.appendToVariable(variables, variableName, value);
            }
            return this;
        }

        @SuppressWarnings("rawtypes")
        public Builder appendValuesToVariable(
            String variableName,
            Collection values
        ) {
            for (Object value : values) {
                View.appendToVariable(variables, variableName, value);
            }
            return this;
        }

        public Builder putKeyValueToVariable(
            String variableName,
            String key,
            Object value
        ) {
            View.putKeyValueToVariable(variables, variableName, key, value);
            return this;
        }

        public Builder putKeyValuesToVariable(
            String variableName,
            Map<String, Object> keyValues
        ) {
            for (Map.Entry<String, Object> e : keyValues.entrySet()) {
                putKeyValueToVariable(variableName, e.getKey(), e.getValue());
            }
            return this;
        }

        public Builder addHeader(String name, Object value) {
            if (headers == null) {
                this.headers = new HashMap<>();
            }
            this.headers.put(name, value.toString());
            return this;
        }

        public Builder addHeaders(Map<String, Object> headers) {
            for (Entry<String, Object> e : headers.entrySet()) {
                addHeader(e.getKey(), e.getValue());
            }
            return this;
        }

        public Builder addCookie(Cookie cookie) {
            if (cookies == null) {
                this.cookies = new LinkedList<>();
            }
            this.cookies.add(cookie);
            return this;
        }

        public Builder addCookie(String name, Object value) {
            return addCookie(new Cookie(name, value.toString()));
        }

        public Builder addCookie(String name, Object value, String path) {
            Cookie cookie = new Cookie(name, value.toString());
            cookie.setPath(path);
            return addCookie(cookie);
        }

        @Override
        public View build() {
            if (template == null) {
                throw new NullPointerException("template can not be null");
            }
            if (cookies == null) {
                cookies = Collections.emptyList();
            }
            if (headers == null) {
                headers = Collections.emptyMap();
            }
            return new View(this);
        }
    }
}
