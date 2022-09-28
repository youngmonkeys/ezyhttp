package com.tvd12.ezyhttp.core.test.net;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyhttp.core.net.MapEncoder;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.TreeMap;

public class MapEncoderTest {

    @Test
    public void encodeToStringTest() throws Exception {
        // given
        TreeMap<String, Object> map = new TreeMap<>();
        map.put("foo", "bar");
        map.put("hello", null);

        // when
        String actual = MapEncoder.encodeToString(map);

        // then
        Asserts.assertEquals(
            actual,
            "foo=bar&hello="
        );
    }
}
