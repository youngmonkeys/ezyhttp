package com.tvd12.ezyhttp.server.core.test.view;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.resources.ResourceFile;
import com.tvd12.ezyhttp.server.core.view.MessageReader;
import com.tvd12.test.assertion.Asserts;

public class MessageReaderTest {

    @Test
    public void test() {
        // given
        MessageReader reader = new MessageReader();

        String folderPath = "messages";

        // when
        Map<String, Properties> map = reader.read(folderPath);

        // then
        Asserts.assertEquals(4, map.size());
        Asserts.assertEquals(2, map.get("").size());
        Asserts.assertEquals(1, map.get("en_US").size());
        Asserts.assertEquals(1, map.get("vi").size());
    }

    @Test
    public void readInJarTest() {
        // given
        MessageReader reader = new MessageReader() {
            protected List<ResourceFile> listResourceFiles(String folderPath) {
                List<ResourceFile> answer = super.listResourceFiles(folderPath);
                answer.add(new ResourceFile("messages/messages.properties", "messages/messages.properties", true));
                return answer;
            }
        };
        
        String folderPath = "messages";
        
        // when
        Map<String, Properties> map = reader.read(folderPath);
        
        // then
        Asserts.assertEquals(4, map.size());
        Asserts.assertEquals(2, map.get("").size());
        Asserts.assertEquals(1, map.get("en_US").size());
        Asserts.assertEquals(1, map.get("vi").size());
    }
}
