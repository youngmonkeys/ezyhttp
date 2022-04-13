package com.tvd12.ezyhttp.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.request.GetRequest;
import com.tvd12.ezyhttp.client.request.PostRequest;
import com.tvd12.ezyhttp.client.request.RequestEntity;
import com.tvd12.ezyhttp.client.test.request.HelloRequest;
import com.tvd12.ezyhttp.client.test.server.TestApplicationBootstrap;
import com.tvd12.ezyhttp.core.annotation.BodyConvert;
import com.tvd12.ezyhttp.core.codec.BodyDeserializer;
import com.tvd12.ezyhttp.core.codec.SingletonStringDeserializer;
import com.tvd12.ezyhttp.core.codec.TextBodyConverter;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.*;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.reflect.MethodInvoker;
import com.tvd12.test.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class HttpClientTest {

	public static void main(String[] args) throws Exception {
		getTest();
		postTest();
	}
	
	protected static void getTest() throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		GetRequest request = new GetRequest()
				.setURL("http://localhost:8081/bye?messages=a,b,c&numbers=1,2,3")
				.setEntity(null)
				.setResponseType(String.class)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request);
		System.out.println(response);
	}
	
	protected static void postTest() throws Exception {
		HttpClient client = HttpClient.builder()
				.build();
		HelloRequest body = new HelloRequest();
		body.setWho("dzung");
		RequestEntity entity = RequestEntity.body(body);
		PostRequest request = new PostRequest()
				.setURL("http://localhost:8081/")
				.setEntity(entity)
				.setResponseType(String.class)
				.setReadTimeout(HttpClient.NO_TIMEOUT)
				.setConnectTimeout(HttpClient.NO_TIMEOUT);
		String response = client.call(request);
		System.out.println(response);
	}

	@Test
	public void httpClientBuilderTest() {
		// given
		int readTimeout = RandomUtil.randomInt();
		int connectionTimeout = RandomUtil.randomInt();
		Object stringConverter = SingletonStringDeserializer.getInstance();
		Object bodyConverter = new MyTextBodyConverter();
		HttpClient.Builder clientBuilder = HttpClient.builder()
			.readTimeout(readTimeout)
			.connectTimeout(connectionTimeout)
			.setStringConverter(stringConverter)
			.addBodyConverter(bodyConverter)
			.addBodyConverters(Collections.singletonList(bodyConverter))
			.addBodyConverters(
				Collections.singletonMap(
					ContentTypes.APPLICATION_JSON,
					bodyConverter
				)
			);

		// when
		HttpClient client = clientBuilder.build();

		// then
		Asserts.assertEquals(
			FieldUtil.getFieldValue(client, "defaultReadTimeout"),
			readTimeout
		);
		Asserts.assertEquals(
			FieldUtil.getFieldValue(client, "defaultConnectTimeout"),
			connectionTimeout
		);
	}
	
	@Test
	public void callTest() throws Exception {
		// given
		HttpClient sut = HttpClient.builder()
				.objectMapper(new Object())
				.objectMapper(new ObjectMapper())
				.build();
		
		PostRequest request = new PostRequest()
				.setConnectTimeout(-1)
				.setReadTimeout(15000)
				.setEntity(
					RequestEntity.builder()
						.body(new TestRequest("Monkey"))
						.header("hello", "world")
						.header("foo", "bar")
						.build()
				)
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/greet"));
		
		// when
		TestResponse actual = sut.call(request);
		
		// then
		TestResponse expectation = new TestResponse("Greet Monkey!");
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void callPostFormTest() throws Exception {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setConnectTimeout(-1)
				.setReadTimeout(15000)
				.setEntity(
					RequestEntity.builder()
						.body(new TestRequest("Young Monkey"))
						.contentType(ContentTypes.APPLICATION_X_WWW_FORM_URLENCODED)
						.build()
				)
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/form"));
		
		// when
		TestResponse actual = sut.call(request);
		
		// then
		TestResponse expectation = new TestResponse("Greet Young Monkey!");
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void postwithNoBody() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/form"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpBadRequestException.class);
	}
	
	@Test
	public void postMethodNotFound() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/404"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpNotFoundException.class);
	}
	
	@Test
	public void postMethodUnauthorized() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/401"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpUnauthorizedException.class);
	}
	
	@Test
	public void postMethodForbidden() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/403"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpForbiddenException.class);
	}
	
	@Test
	public void postMethodNotAcceptable() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/406"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpNotAcceptableException.class);
	}
	
	@Test
	public void postMethodTimeout() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/408"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpRequestTimeoutException.class);
	}
	
	@Test
	public void postMethodConflict() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/409"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpConflictException.class);
	}
	
	@Test
	public void postMethodUnsupportedMediaType() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/415"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpUnsupportedMediaTypeException.class);
	}
	
	@Test
	public void postMethodServerInternalError() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/500"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpInternalServerErrorException.class);
	}
	
	@Test
	public void postMethodServer501() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/501"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpRequestException.class);
	}
	
	@Test
	public void postMethodNotAllow() {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setResponseType(StatusCodes.OK, TestResponse.class)
				.setURL(URI.create("http://127.0.0.1:18081/405"));
		
		// when
		Throwable e = Asserts.assertThrows(() -> sut.call(request));
		
		// then
		Asserts.assertThat(e).isEqualsType(HttpMethodNotAllowedException.class);
	}
	
	@Test
	public void deserializeResponseBodyString() throws Exception {
		// given
		HttpClient sut = HttpClient.builder()
				.build();
		
		PostRequest request = new PostRequest()
				.setResponseType(TestResponse.class)
				.setEntity(
					RequestEntity.builder()
						.body(new TestRequest("Monkey"))
						.build()
				)
				.setResponseType(StatusCodes.OK, String.class)
				.setURL(URI.create("http://127.0.0.1:18081/greet"));
		
		// when
		String actual = sut.call(request);
		
		// then
		String expectation = "{\"message\":\"Greet Monkey!\"}";
		Asserts.assertEquals(expectation, actual);
	}
	
	@Test
	public void deserializeResponseBodyStringFailed() throws Exception {
		// given
		String contentType = RandomUtil.randomShortAlphabetString();
		int contentLength = RandomUtil.randomSmallInt();
		InputStream inputStream = mock(InputStream.class);
		
		
		BodyDeserializer deserializer = mock(BodyDeserializer.class);
		IOException exception = new IOException("just test");
		when(
			deserializer.deserializeToString(inputStream, contentLength)
		).thenThrow(exception);
		
		HttpClient sut = HttpClient.builder()
				.addBodyConverter(contentType, deserializer)
				.build();

		// when
		Throwable e = Asserts.assertThrows(() -> 
			MethodInvoker.create()
				.object(sut)
				.method("deserializeResponseBody")
				.param(String.class, contentType)
				.param(int.class, contentLength)
				.param(InputStream.class, inputStream)
				.param(Class.class, null)
				.call()
		);
		
		// then
		Asserts.assertThat(e.getCause().getCause()).isEqualsTo(exception);
	}
	
	@Test
	public void tryDeserializeResponseBodyStringSuccess() throws Exception {
		// given
		String contentType = RandomUtil.randomShortAlphabetString();
		int contentLength = RandomUtil.randomSmallInt();
		InputStream inputStream = mock(InputStream.class);
		

		String data = RandomUtil.randomShortAlphabetString();
		BodyDeserializer deserializer = mock(BodyDeserializer.class);
		when(
			deserializer.deserializeToString(inputStream, contentLength)
		).thenReturn(data);
		
		Map<String, String> map = new HashMap<>();
		when(deserializer.deserialize(data, Map.class)).thenReturn(map);
		
		HttpClient sut = HttpClient.builder()
				.addBodyConverter(contentType, deserializer)
				.build();

		// when
		Map<String, String> actual = MethodInvoker.create()
				.object(sut)
				.method("deserializeResponseBody")
				.param(String.class, contentType)
				.param(int.class, contentLength)
				.param(InputStream.class, inputStream)
				.param(Class.class, null)
				.call();
		
		// then
		Asserts.assertEquals(map, actual);
	}
	
	@Test
	public void tryDeserializeResponseBodyStringNull() {
		// given
		String contentType = RandomUtil.randomShortAlphabetString();
		int contentLength = RandomUtil.randomSmallInt();
		InputStream inputStream = mock(InputStream.class);
		

		BodyDeserializer deserializer = mock(BodyDeserializer.class);
		
		HttpClient sut = HttpClient.builder()
				.addBodyConverter(contentType, deserializer)
				.build();

		// when
		Map<String, String> actual = MethodInvoker.create()
				.object(sut)
				.method("deserializeResponseBody")
				.param(String.class, contentType)
				.param(int.class, contentLength)
				.param(InputStream.class, inputStream)
				.param(Class.class, null)
				.call();
		
		// then
		Asserts.assertNull(actual);
	}
	
	@Test
    public void downloadToFileNotFoundTest() {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd/not-found-here";
        
        HttpClient sut = HttpClient.builder()
                .build();
        
        // when
        Throwable e = Asserts.assertThrows(
                () -> sut.download(fileUrl, new File("test-output/no-commit"))
        );
        
        // then
        Asserts.assertEqualsType(e, HttpNotFoundException.class);
    }
    
    @Test
    public void downloadToOutputStreamNotFoundTest() {
        // given
        String fileUrl = "https://resources.tvd12.com/ezy-settings-1.0.0.xsd/not-found-here";
        
        HttpClient sut = HttpClient.builder()
                .build();
        
        OutputStream outputStream = mock(OutputStream.class);
        
        // when
        Throwable e = Asserts.assertThrows(
                () -> sut.download(fileUrl, outputStream)
        );
        
        // then
        Asserts.assertEqualsType(e, HttpNotFoundException.class);
    }
    
    @Test
    public void getDownloadFileNameTest() {
        // given
        HttpClient sut = HttpClient.builder()
                .build();
        
        String contentDisposition = "Content-Disposition: attachment; filename=\"filename.jpg\"";
        
        // when
        String actual = MethodInvoker.create()
                .object(sut)
                .method("getDownloadFileName")
                .param("fileUrl")
                .param(contentDisposition)
                .call();
        
        // then
        Asserts.assertEquals(actual, "filename.jpg");
    }
    
    @Test
    public void getDownloadFileNameWithEmptyDispositionTest() {
        // given
        HttpClient sut = HttpClient.builder()
                .build();
        
        String contentDisposition = "";
        
        // when
        String actual = MethodInvoker.create()
                .object(sut)
                .method("getDownloadFileName")
                .param("https://example.com/file.jpg")
                .param(contentDisposition)
                .call();
        
        // then
        Asserts.assertEquals(actual, "file.jpg");
    }
    
    @Test
    public void processDownloadErrorInputStreamIsNull() {
        // given
        HttpClient sut = HttpClient.builder()
                .build();
        
        HttpURLConnection connection = mock(HttpURLConnection.class);
        
        
        // when
        Exception e = MethodInvoker.create()
            .object(sut)
            .method("processDownloadError")
            .param(HttpURLConnection.class, connection)
            .param(String.class, "https://example.com/file.jpg")
            .param(int.class, 404)
            .call();
        
        // then
        Asserts.assertEqualsType(e, HttpNotFoundException.class);
        verify(connection, times(1)).getErrorStream();
    }
    
    @Test
    public void getDownloadFileNameWithemiColonTest() {
        // given
        String contentDisposition = "filename=hello;";
        
        // when
        String actual = HttpClient.getDownloadFileName("", contentDisposition);
        
        // then
        Asserts.assertEquals(actual, "hello");
    }
    
    @Test
    public void getDownloadFileNameWithQuotesTest() {
        // given
        String contentDisposition = "filename=\"hello\";";
        
        // when
        String actual = HttpClient.getDownloadFileName("", contentDisposition);
        
        // then
        Asserts.assertEquals(actual, "hello");
    }
    
    @Test
    public void getDownloadFileNameWithQuoteTest() {
        // given
        String contentDisposition = "filename='hello';";
        
        // when
        String actual = HttpClient.getDownloadFileName("", contentDisposition);
        
        // then
        Asserts.assertEquals(actual, "hello");
    }
    
    @Test
    public void getDownloadFileNameWithMaxLengthTest() {
        // given
        String contentDisposition = "filename=hello";
        
        // when
        String actual = HttpClient.getDownloadFileName("", contentDisposition);
        
        // then
        Asserts.assertEquals(actual, "hello");
    }
	
	@BeforeTest
	public void setUp() {
		TestApplicationBootstrap.getInstance().start();
	}
	
	@Getter
	@AllArgsConstructor
	public static class TestRequest {
		private String who;
	}
	
	@Data
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TestResponse {
		private String message;
	}

	@BodyConvert(ContentTypes.APPLICATION_JSON)
	public static class MyTextBodyConverter extends TextBodyConverter {}
}
