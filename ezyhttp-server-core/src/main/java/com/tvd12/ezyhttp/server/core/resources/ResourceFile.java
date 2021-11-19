package com.tvd12.ezyhttp.server.core.resources;

import com.tvd12.ezyfox.util.EzyFileUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResourceFile {

    private final String relativePath;
    private final String fullPath;
    private final boolean inJar;
    
    public boolean isFileNameMatches(String pattern) {
        return EzyFileUtil.getFileName(relativePath).matches(pattern);
    }
}
