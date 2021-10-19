package com.tvd12.ezyhttp.server.core.test.manager;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.server.core.manager.ComponentManager;
import com.tvd12.test.assertion.Asserts;

public class ComponentManagerTest {

    @Test
    public void test() {
        // given
        ComponentManager componentManager = ComponentManager.getInstance();
        componentManager.setExposeMangementURIs(true);
        
        // when
        // then
        Asserts.assertTrue(componentManager.isExposeMangementURIs());
        componentManager.destroy();
    }
}
