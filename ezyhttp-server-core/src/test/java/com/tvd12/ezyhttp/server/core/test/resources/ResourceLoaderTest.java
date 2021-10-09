package com.tvd12.ezyhttp.server.core.test.resources;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import org.testng.annotations.Test;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyfox.function.EzySupplier;
import com.tvd12.ezyhttp.server.core.resources.ResourceLoader;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodInvoker;

public class ResourceLoaderTest {

	@Test
	public void urlNull() {
		// given
		ResourceLoader sut = new ResourceLoader();
		
		// when
		List<String> listResources = sut.listResources("not found");
		
		// then
		Asserts.assertTrue(listResources.isEmpty());
	}
	
	@Test
	public void urlPathEmpty() {
		// given
		ResourceLoader sut = new ResourceLoader() {
			@Override
			protected Set<URL> getResourceURLs(String resource) {
				try {
					return Sets.newHashSet(new URL("http://locahost"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
					throw new IllegalArgumentException(e);
				}
			}
		};
		
		// when
		List<String> listResources = sut.listResources("application.yaml");
		
		// then
		Asserts.assertTrue(listResources.isEmpty());
	}
	
	@Test
	public void regexTest() {
		// given
		ResourceLoader sut = new ResourceLoader();
		
		// when
		List<String> listResources = sut.listResources("static", Sets.newHashSet("^static/css/.+"));
		
		// then
		Asserts.assertEquals(2, listResources.size());
	}
	
	@Test
	public void addURLsToSetTest() {
	    // given
	    ResourceLoader sut = new ResourceLoader();
	    
	    Set<URL> answer = new HashSet<>();
	    Exception exception = new Exception("just test");
	    EzySupplier<Enumeration<URL>> supplier = new EzySupplier<Enumeration<URL>>() {
            @Override
            public Enumeration<URL> get() throws Exception {
                throw exception;
            }
	        
        };
	    
	    // when
	    MethodInvoker.create()
	        .object(sut)
	        .method("addURLsToSet")
	        .param(Set.class, answer)
	        .param(EzySupplier.class, supplier)
	        .invoke();
	    
	    // then
	    Asserts.assertTrue(answer.isEmpty());
	}
	
	@Test
    public void addURLsToSetURLsIsNull() {
        // given
        ResourceLoader sut = new ResourceLoader();
        
        Set<URL> answer = new HashSet<>();
        
        // when
        MethodInvoker.create()
            .object(sut)
            .method("addURLsToSet")
            .param(Set.class, answer)
            .param(Enumeration.class, null)
            .invoke();
        
        // then
        Asserts.assertTrue(answer.isEmpty());
    }
	
	@Test
    public void loadFromJarTest() {
        // given
        ResourceLoader sut = new ResourceLoader();
        
        // when
        List<String> listResources = sut.listResources("com/tvd12", Sets.newHashSet("^static/css/.+"));
        
        // then
        Asserts.assertEquals(0, listResources.size());
    }
	
	@Test
    public void loadFromJarMatchTest() {
        // given
        ResourceLoader sut = new ResourceLoader();
        
        // when
        List<String> listResources = sut.listResources("com/tvd12/ezyfox/net", Sets.newHashSet(".+"));
        
        // then
        Asserts.assertFalse(listResources.isEmpty());
    }
	
	@Test
	public void listFileEmptyTest() {
	    // given
	    File file = new File("unknow file");
	    ResourceLoader sut = new ResourceLoader();
	    
	    // when
	    File[] actual = MethodInvoker.create()
	            .object(sut)
	            .method("listFile")
	            .param(File.class, file)
	            .invoke(File[].class);
	    
	    // then
	    Asserts.assertZero(actual.length);
	}
	
	@Test
    public void isNotFileElement() {
        // given
        String filePath = "file\\";
        ResourceLoader sut = new ResourceLoader();
        
        // when
        boolean actual = MethodInvoker.create()
                .object(sut)
                .method("isFileElement")
                .param(String.class, filePath)
                .invoke(Boolean.class);
        
        // then
        Asserts.assertFalse(actual);
    }
	
	@Test
    public void getJarFileNullDueToException() {
        // given
        String filePath = "#$%^&*";
        ResourceLoader sut = new ResourceLoader();
        
        // when
        JarFile actual = MethodInvoker.create()
                .object(sut)
                .method("getJarFile")
                .param(String.class, filePath)
                .invoke(JarFile.class);
        
        // then
        Asserts.assertNull(actual);
    }
	
	@Test
    public void loadFromFolder() {
        // given
        ResourceLoader sut = new ResourceLoader();
        
        // when
        List<String> listResources = sut.listResources("src/main/resources");
        
        // then
        Asserts.assertEquals(new File("src/main/resources").list().length, listResources.size());
    }
}
