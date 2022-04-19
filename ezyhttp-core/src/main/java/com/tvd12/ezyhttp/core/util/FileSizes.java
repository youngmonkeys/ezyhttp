package com.tvd12.ezyhttp.core.util;

public final class FileSizes {

    private FileSizes() {
    }

    public static long toByteSize(String value) {
        String lowercase = value.toLowerCase();
        if (value.length() > 2) {
            if (lowercase.endsWith("kb")) {
                return subSizeStringToLong(value, 2) * 1024;
            }
            if (lowercase.endsWith("mb")) {
                return subSizeStringToLong(value, 2) * 1024 * 1024;
            }
            if (lowercase.endsWith("gb")) {
                return subSizeStringToLong(value, 2) * 1024 * 1024 * 1024;
            }
            if (lowercase.endsWith("tb")) {
                return subSizeStringToLong(value, 2) * 1024 * 1024 * 1024 * 1024;
            }
        }
        if (value.length() > 1) {
            if (lowercase.endsWith("b")) {
                return subSizeStringToLong(value, 1);
            }
        }
        throw new IllegalArgumentException("size must follow template: [value][B|KB|MB|GB|TB]");
    }

    private static long subSizeStringToLong(String value, int suffixSize) {
        try {
            return Long.parseLong(value.substring(0, value.length() - suffixSize));
        } catch (Exception e) {
            throw new IllegalArgumentException("size must follow template: [value][B|KB|MB|GB|TB]", e);
        }
    }

}
