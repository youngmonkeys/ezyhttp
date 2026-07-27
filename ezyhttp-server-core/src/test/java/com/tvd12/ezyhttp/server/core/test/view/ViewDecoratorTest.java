package com.tvd12.ezyhttp.server.core.test.view;

import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewDecorator;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;

public class ViewDecoratorTest {

    @Test
    public void test() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        View view = View.builder()
            .template("aaa")
            .build();
        ViewDecorator instance = new ViewDecorator() {};

        // when
        // then
        instance.decorate(request, response, view);
    }
}
