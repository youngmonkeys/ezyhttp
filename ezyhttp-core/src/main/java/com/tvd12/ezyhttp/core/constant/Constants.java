package com.tvd12.ezyhttp.core.constant;

public final class Constants {

    public static final String EMPTY_STRING = "";
    public static final String DEFAULT_URI = "/";
    public static final String DEFAULT_QL_GROUP_NAME = "default";
    public static final String EXTENSION_CLASS = ".class";
    public static final String[] DEFAULT_PROPERTIES_FILES = new String[]{
        "application.properties",
        "application.yaml"
    };
    public static final String DEFAULT_PACKAGE_TO_SCAN = "com.tvd12.ezyhttp.server";

    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;

    private Constants() {}
}
