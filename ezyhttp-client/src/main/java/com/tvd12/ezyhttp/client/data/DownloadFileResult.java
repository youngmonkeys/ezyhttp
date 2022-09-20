package com.tvd12.ezyhttp.client.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DownloadFileResult {
    private final String originalFileName;
    private final String newFileName;
}
