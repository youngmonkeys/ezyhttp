package com.tvd12.ezyhttp.core.test.response;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.data.MultiValueMap;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.test.assertion.Asserts;

public class ResponseEntityTest {

    @Test
    public void buildTest() {
        // given
        int status = StatusCodes.ACCEPTED;
        Object body = "Hello World";
        
        // when
        ResponseEntity sut = ResponseEntity.builder()
                .textPlain(body)
                .header("hello", Arrays.asList("world", "galaxy"))
                .header("foo", "bar")
                .headers(Collections.singletonMap("yes", "no"))
                .status(status)
                .build();
        
        // then
        Asserts.assertEquals(status, sut.getStatus());
        Asserts.assertEquals("world", sut.getHeader("hello"));
        Asserts.assertEquals("bar", sut.getHeader("foo"));
        Asserts.assertEquals("no", sut.getHeader("yes"));
        Asserts.assertEquals(ContentTypes.TEXT_PLAIN, sut.getContentType());
        Asserts.assertNull(sut.getHeader("nothing"));
        Asserts.assertEquals(body, sut.getBody());
        System.out.println(sut);
    }
    
    
    @Test
    public void createByStatusHeadersAndBody() {
        // given
        int status = StatusCodes.ACCEPTED;
        Map<String, List<String>> headers = new HashMap<>();
        headers.put("hello", Arrays.asList("world", "galaxy"));
        headers.put("foo", Arrays.asList("bar", "animal"));
        Object body = "Hello World";
        
        // when
        ResponseEntity sut = new ResponseEntity(status, headers, body);
        
        // then
        Asserts.assertEquals(status, sut.getStatus());
        Asserts.assertEquals(new MultiValueMap(headers), sut.getHeaders());
        Asserts.assertEquals("world", sut.getHeader("hello"));
        Asserts.assertEquals(body, sut.getBody());
    }
    
    @Test
    public void createByStatus() {
        // given
        int status = StatusCodes.ACCEPTED;
        
        // when
        ResponseEntity sut = ResponseEntity.status(status).build();
        
        // then
        Asserts.assertEquals(status, sut.getStatus());
    }
    
    @Test
    public void createByStatusAndBody() {
        // given
        int status = StatusCodes.ACCEPTED;
        Object body = "Hello World";
        
        // when
        ResponseEntity sut = ResponseEntity.of(status, body).build();
        
        // then
        Asserts.assertEquals(status, sut.getStatus());
        Asserts.assertEquals(body, sut.getBody());
    }
    
    @Test
    public void createByStatusHeadersAndBody2() {
        // given
        int status = StatusCodes.ACCEPTED;
        Object body = "Hello World";
        
        // when
        ResponseEntity sut = ResponseEntity.create(status, body);
        
        // then
        Asserts.assertEquals(status, sut.getStatus());
        Asserts.assertEquals(body, sut.getBody());
    }
    
    @Test
    public void createByOk() {
        // given
        // when
        ResponseEntity sut = ResponseEntity.ok();
        
        // then
        Asserts.assertEquals(StatusCodes.OK, sut.getStatus());
    }
    
    @Test
    public void createByOkAndBody() {
        // given
        Object body = "Hello World";
        
        // when
        ResponseEntity sut = ResponseEntity.ok(body);
        
        // then
        Asserts.assertEquals(body, sut.getBody());
    }
    
    @Test
    public void createByBadRequest() {
        // given
        // when
        ResponseEntity sut = ResponseEntity.badRequest();
        
        // then
        Asserts.assertEquals(StatusCodes.BAD_REQUEST, sut.getStatus());
    }
    
    @Test
    public void createByBadRequestAndBody() {
        // given
        Object body = "Hello World";
        
        // when
        ResponseEntity sut = ResponseEntity.badRequest(body);
        
        // then
        Asserts.assertEquals(StatusCodes.BAD_REQUEST, sut.getStatus());
        Asserts.assertEquals(body, sut.getBody());
    }
    
    @Test
    public void createByNotFound() {
        // given
        // when
        ResponseEntity sut = ResponseEntity.notFound();
        
        // then
        Asserts.assertEquals(StatusCodes.NOT_FOUND, sut.getStatus());
    }
    
    @Test
    public void createByNotFoundAndBody() {
        // given
        Object body = "Hello World";
        
        // when
        ResponseEntity sut = ResponseEntity.notFound(body);
        
        // then
        Asserts.assertEquals(StatusCodes.NOT_FOUND, sut.getStatus());
        Asserts.assertEquals(body, sut.getBody());
    }
    
    @Test
    public void headersNull() {
        // given
        // when
        ResponseEntity sut = ResponseEntity.ok();
        
        // then
        Asserts.assertNull(sut.getHeader("nothing"));
        System.out.println(sut);
    }
    
    @Test
    public void noContentTest() {
        // given
        // when
        ResponseEntity sut = ResponseEntity.noContent();
        
        // then
        Asserts.assertEquals(StatusCodes.NO_CONTENT, sut.getStatus());
    }
}
