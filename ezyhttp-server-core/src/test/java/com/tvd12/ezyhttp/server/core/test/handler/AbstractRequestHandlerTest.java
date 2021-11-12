package com.tvd12.ezyhttp.server.core.test.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;

import javax.servlet.AsyncContext;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.data.BodyData;
import com.tvd12.ezyhttp.core.exception.DeserializeBodyException;
import com.tvd12.ezyhttp.core.exception.DeserializeCookieException;
import com.tvd12.ezyhttp.core.exception.DeserializeHeaderException;
import com.tvd12.ezyhttp.core.exception.DeserializeParameterException;
import com.tvd12.ezyhttp.core.exception.DeserializePathVariableException;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.server.core.handler.AbstractRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.MethodInvoker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AbstractRequestHandlerTest {
	
	@Test
	public void handleTest() throws Exception {
		// given
		ExResponse response = new ExResponse("Hello World");
		
		ExRequestHandler sut = new ExRequestHandler() {
			public Object handleRequest(RequestArguments arguments) throws Exception {
				return response;
			}
		};
		
		RequestArguments requestArguments = mock(RequestArguments.class);
		
		// when
		Object actual = sut.handle(requestArguments);
		
		// then
		Asserts.assertEquals(response, actual);
		Asserts.assertNull(sut.getHandlerMethod());
	}
	
	@Test
	public void handleException() throws Exception {
		// given
		ExResponse response = new ExResponse("Hello World");
		
		ExRequestHandler sut = new ExRequestHandler() {
			@Override
			public Object handleRequest(RequestArguments arguments) throws Exception {
				throw new Exception("just test");
			}
			@Override
			protected Object handleException(RequestArguments arguments, Exception e) throws Exception {
				return response;
			}
		};
		
		RequestArguments requestArguments = mock(RequestArguments.class);
		
		// when
		Object actual = sut.handle(requestArguments);
		
		// then
		Asserts.assertEquals(response, actual);
	}
	
	@Test
    public void handleExceptionAndAsync() throws Exception {
        // given
        ExResponse response = new ExResponse("Hello World");
        
        ExRequestHandler sut = new ExRequestHandler() {
            @Override
            public Object handleRequest(RequestArguments arguments) throws Exception {
                throw new Exception("just test");
            }
            @Override
            protected Object handleException(RequestArguments arguments, Exception e) throws Exception {
                return response;
            }
        };
        
        RequestArguments requestArguments = mock(RequestArguments.class);
        when(requestArguments.isAsyncStarted()).thenReturn(true);
        
        AsyncContext asyncContext = mock(AsyncContext.class);
        when(requestArguments.getAsynContext()).thenReturn(asyncContext);
        
        // when
        Object actual = sut.handle(requestArguments);
        
        // then
        Asserts.assertEquals(response, actual);
        verify(asyncContext, times(1)).complete();
    }
	
	@Test
	public void deserializeHeaderTest() {
		// given
		int index = 0;
		String value = "1";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		int actual = (int)MethodInvoker.create()
				.object(sut)
				.method("deserializeHeader")
				.param(int.class, index)
				.param(String.class, value)
				.param(Class.class, type)
				.param(Class.class, genericType)
				.invoke();
		
		// then
		Asserts.assertEquals(1, actual);
	}
	
	@Test
	public void deserializeHeaderFailed() {
		// given
		int index = 0;
		String value = "abc";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		Throwable e = Asserts.assertThrows(() -> {
			MethodInvoker.create()
			.object(sut)
			.method("deserializeHeader")
			.param(int.class, index)
			.param(String.class, value)
			.param(Class.class, type)
			.param(Class.class, genericType)
			.invoke();
		});
		
		// then
		Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializeHeaderException.class);
	}
	
	@Test
	public void deserializeHeader2Test() {
		// given
		String name = "name";
		String value = "1";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		int actual = (int)MethodInvoker.create()
				.object(sut)
				.method("deserializeHeader")
				.param(String.class, name)
				.param(String.class, value)
				.param(Class.class, type)
				.param(Class.class, genericType)
				.invoke();
		
		// then
		Asserts.assertEquals(1, actual);
	}
	
	@Test
	public void deserializeHeader2Failed() {
		// given
		String name = "name";
		String value = "abc";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		Throwable e = Asserts.assertThrows(() -> {
			MethodInvoker.create()
			.object(sut)
			.method("deserializeHeader")
			.param(String.class, name)
			.param(String.class, value)
			.param(Class.class, type)
			.param(Class.class, genericType)
			.invoke();
		});
		
		// then
		Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializeHeaderException.class);
	}
	
	@Test
	public void deserializeParameterTest() {
		// given
		int index = 0;
		String value = "1";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		int actual = (int)MethodInvoker.create()
				.object(sut)
				.method("deserializeParameter")
				.param(int.class, index)
				.param(String.class, value)
				.param(Class.class, type)
				.param(Class.class, genericType)
				.invoke();
		
		// then
		Asserts.assertEquals(1, actual);
	}
	
	@Test
	public void deserializeParameterFailed() {
		// given
		int index = 0;
		String value = "abc";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		Throwable e = Asserts.assertThrows(() -> {
			MethodInvoker.create()
			.object(sut)
			.method("deserializeParameter")
			.param(int.class, index)
			.param(String.class, value)
			.param(Class.class, type)
			.param(Class.class, genericType)
			.invoke();
		});
		
		// then
		Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializeParameterException.class);
	}
	
	@Test
	public void deserializeParameter2Test() {
		// given
		String name = "name";
		String value = "1";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		int actual = (int)MethodInvoker.create()
				.object(sut)
				.method("deserializeParameter")
				.param(String.class, name)
				.param(String.class, value)
				.param(Class.class, type)
				.param(Class.class, genericType)
				.invoke();
		
		// then
		Asserts.assertEquals(1, actual);
	}
	
	@Test
	public void deserializeParameter2Failed() {
		// given
		String name = "name";
		String value = "abc";
		Class<?> type = int.class;
		Class<?> genericType = null;
		
		ExRequestHandler sut = new ExRequestHandler();
		
		// when
		Throwable e = Asserts.assertThrows(() -> {
			MethodInvoker.create()
			.object(sut)
			.method("deserializeParameter")
			.param(String.class, name)
			.param(String.class, value)
			.param(Class.class, type)
			.param(Class.class, genericType)
			.invoke();
		});
		
		// then
		Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializeParameterException.class);
	}
	
	@Test
    public void deserializePathVariableTest() {
        // given
        int index = 0;
        String value = "1";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        int actual = (int)MethodInvoker.create()
                .object(sut)
                .method("deserializePathVariable")
                .param(int.class, index)
                .param(String.class, value)
                .param(Class.class, type)
                .param(Class.class, genericType)
                .invoke();
        
        // then
        Asserts.assertEquals(1, actual);
    }
    
    @Test
    public void deserializePathVariableFailed() {
        // given
        int index = 0;
        String value = "abc";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        Throwable e = Asserts.assertThrows(() -> {
            MethodInvoker.create()
            .object(sut)
            .method("deserializePathVariable")
            .param(int.class, index)
            .param(String.class, value)
            .param(Class.class, type)
            .param(Class.class, genericType)
            .invoke();
        });
        
        // then
        Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializePathVariableException.class);
    }
    
    @Test
    public void deserializePathVariable2Test() {
        // given
    	String name = "name";
        String value = "1";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        int actual = (int)MethodInvoker.create()
                .object(sut)
                .method("deserializePathVariable")
                .param(String.class, name)
                .param(String.class, value)
                .param(Class.class, type)
                .param(Class.class, genericType)
                .invoke();
        
        // then
        Asserts.assertEquals(1, actual);
    }
    
    @Test
    public void deserializePathVariable2Failed() {
        // given
        String name = "name";
        String value = "abc";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        Throwable e = Asserts.assertThrows(() -> {
            MethodInvoker.create()
            .object(sut)
            .method("deserializePathVariable")
            .param(String.class, name)
            .param(String.class, value)
            .param(Class.class, type)
            .param(Class.class, genericType)
            .invoke();
        });
        
        // then
        Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializePathVariableException.class);
    }
    
    @Test
    public void deserializeCookieTest() {
        // given
        int index = 0;
        String value = "1";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        int actual = (int)MethodInvoker.create()
                .object(sut)
                .method("deserializeCookie")
                .param(int.class, index)
                .param(String.class, value)
                .param(Class.class, type)
                .param(Class.class, genericType)
                .invoke();
        
        // then
        Asserts.assertEquals(1, actual);
    }
    
    @Test
    public void deserializeCookieFailed() {
        // given
        int index = 0;
        String value = "abc";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        Throwable e = Asserts.assertThrows(() -> {
            MethodInvoker.create()
            .object(sut)
            .method("deserializeCookie")
            .param(int.class, index)
            .param(String.class, value)
            .param(Class.class, type)
            .param(Class.class, genericType)
            .invoke();
        });
        
        // then
        Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializeCookieException.class);
    }
    
    @Test
    public void deserializeCookie2Test() {
        // given
        String name = "name";
        String value = "1";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        int actual = (int)MethodInvoker.create()
                .object(sut)
                .method("deserializeCookie")
                .param(String.class, name)
                .param(String.class, value)
                .param(Class.class, type)
                .param(Class.class, genericType)
                .invoke();
        
        // then
        Asserts.assertEquals(1, actual);
    }
    
    @Test
    public void deserializeCookie2Failed() {
        // given
        String name = "name";
        String value = "abc";
        Class<?> type = int.class;
        Class<?> genericType = null;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        Throwable e = Asserts.assertThrows(() -> {
            MethodInvoker.create()
            .object(sut)
            .method("deserializeCookie")
            .param(String.class, name)
            .param(String.class, value)
            .param(Class.class, type)
            .param(Class.class, genericType)
            .invoke();
        });
        
        // then
        Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializeCookieException.class);
    }
    
    @Test
    public void deserializeBodyTest() throws Exception {
        // given
    	BodyData bodyData = mock(BodyData.class);
    	when(bodyData.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
    	
    	byte[] bytes = "{\"who\":\"Monkey\"}".getBytes();
    	when(bodyData.getInputStream()).thenReturn(new ByteArrayInputStream(bytes));
    	
        Class<?> type = ExRequest.class;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        ExRequest actual = MethodInvoker.create()
                .object(sut)
                .method("deserializeBody")
                .param(BodyData.class, bodyData)
                .param(Class.class, type)
                .invoke(ExRequest.class);
        
        // then
        Asserts.assertEquals(new ExRequest("Monkey"), actual);
        
        verify(bodyData, times(1)).getContentType();
    }
	
    @Test
    public void deserializeBodyDueToBody() throws Exception {
        // given
    	BodyData bodyData = mock(BodyData.class);
    	when(bodyData.getContentType()).thenReturn(ContentTypes.APPLICATION_JSON);
    	
    	byte[] bytes = "123abc".getBytes();
    	when(bodyData.getInputStream()).thenReturn(new ByteArrayInputStream(bytes));
    	
        Class<?> type = ExRequest.class;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        Throwable e = Asserts.assertThrows(() -> {
        	 MethodInvoker.create()
             .object(sut)
             .method("deserializeBody")
             .param(BodyData.class, bodyData)
             .param(Class.class, type)
             .invoke(ExRequest.class);
        });
        
        // then
        Asserts.assertThat(e.getCause().getCause()).isEqualsType(DeserializeBodyException.class);
        
        verify(bodyData, times(1)).getContentType();
    }
	
    @Test
    public void deserializeBodyDueToContentTypeIsNull() throws Exception {
        // given
    	BodyData bodyData = mock(BodyData.class);
    	
    	byte[] bytes = "123abc".getBytes();
    	when(bodyData.getInputStream()).thenReturn(new ByteArrayInputStream(bytes));
    	
        Class<?> type = ExRequest.class;
        
        ExRequestHandler sut = new ExRequestHandler();
        
        // when
        Throwable e = Asserts.assertThrows(() -> {
        	 MethodInvoker.create()
             .object(sut)
             .method("deserializeBody")
             .param(BodyData.class, bodyData)
             .param(Class.class, type)
             .invoke(ExRequest.class);
        });
        
        // then
        Asserts.assertThat(e.getCause().getCause()).isEqualsType(HttpBadRequestException.class);
        
        verify(bodyData, times(1)).getContentType();
    }
    
	public static class ExRequestHandler extends AbstractRequestHandler {

		@Override
		public HttpMethod getMethod() {
			return HttpMethod.POST;
		}

		@Override
		public String getRequestURI() {
			return "/post";
		}

		@Override
		public String getResponseContentType() {
			return ContentTypes.APPLICATION_JSON;
		}

		@Override
		protected Object handleRequest(RequestArguments arguments) throws Exception {
			return null;
		}

		@Override
		protected Object handleException(RequestArguments arguments, Exception e) throws Exception {
			return null;
		}
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ExRequest {
		private String who;
	}
	
	@Data
	@AllArgsConstructor
	public static class ExResponse {
		private String message;
	}
}
