package com.tvd12.ezyhttp.server.core.resources;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Resource {
    private final String path;
    private final String uri;
    private final String extension;
    private final String fullPath;
}
