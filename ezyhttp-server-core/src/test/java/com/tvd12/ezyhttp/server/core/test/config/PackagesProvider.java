package com.tvd12.ezyhttp.server.core.test.config;

import java.util.Collections;
import java.util.Set;

import com.tvd12.ezyfox.bean.EzyPackagesToScanProvider;

public class PackagesProvider implements EzyPackagesToScanProvider {

    @Override
    public Set<String> provide() {
        return Collections.singleton("com.tvd12.ezyhttp.server.core.test");
    }
}
