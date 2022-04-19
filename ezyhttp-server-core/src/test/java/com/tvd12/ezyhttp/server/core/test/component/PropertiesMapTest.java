package com.tvd12.ezyhttp.server.core.test.component;

import java.util.Collections;
import java.util.Map;

import com.tvd12.ezyfox.bean.EzyPropertiesMap;
import com.tvd12.ezyfox.bean.annotation.EzySingleton;

@EzySingleton
public class PropertiesMapTest implements EzyPropertiesMap {

    @Override
    public Map<String, String> keyMap() {
        return Collections.singletonMap("first", "one");
    }

}
