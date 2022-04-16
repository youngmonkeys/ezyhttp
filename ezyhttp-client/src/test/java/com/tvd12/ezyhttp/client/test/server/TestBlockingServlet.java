package com.tvd12.ezyhttp.client.test.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.codec.JsonBodyConverter;
import com.tvd12.ezyhttp.core.constant.StatusCodes;

public class TestBlockingServlet extends HttpServlet {
	private static final long serialVersionUID = 1321397014207226911L;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final JsonBodyConverter jsonBodyConverter = new JsonBodyConverter(objectMapper);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doHandle(req, resp);
	}
	
	protected void doHandle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		if (uri.equals("/greet")) {
			String who = req.getParameter("who");
			if(who == null && !req.getMethod().equalsIgnoreCase("GET")) {
				who = (String)jsonBodyConverter.deserialize(req.getInputStream(), Map.class)
						.get("who");
			}
			
			if(EzyStrings.isNoContent(who)) {
				resp.getOutputStream().write("bad request".getBytes());
				resp.setStatus(StatusCodes.BAD_REQUEST);
				return;
			}
			
			String message = "{\"message\":\"Greet " + who + "!\"}";
			resp.getOutputStream().write(message.getBytes());
			resp.setStatus(StatusCodes.OK);
		}
		else if(uri.equals("/form")) {
			String who = req.getParameter("who");
			if(EzyStrings.isNoContent(who)) {
				resp.getOutputStream().write("bad request".getBytes());
				resp.setStatus(StatusCodes.BAD_REQUEST);
				return;
			}
			
			String message = "{\"message\":\"Greet " + who + "!\"}";
			resp.getOutputStream().write(message.getBytes());
			resp.setStatus(StatusCodes.OK);
		}
		else if(uri.equals("/no-content-error")) {
			resp.setContentLength(1);
			resp.getOutputStream().write(new byte[] {0});
			resp.setStatus(StatusCodes.OK);
		}
		else if(uri.equals("/401")) {
			resp.setStatus(StatusCodes.UNAUTHORIZED);
		}
		else if(uri.equals("/403")) {
			resp.setStatus(StatusCodes.FORBIDDEN);
		}
		else if(uri.equals("/404")) {
			resp.setStatus(StatusCodes.NOT_FOUND);
		}
		else if(uri.equals("/405")) {
			resp.setStatus(StatusCodes.METHOD_NOT_ALLOWED);
		}
		else if(uri.equals("/406")) {
			resp.setStatus(StatusCodes.NOT_ACCEPTABLE);
		}
		else if(uri.equals("/408")) {
			resp.setStatus(StatusCodes.REQUEST_TIMEOUT);
		}
		else if(uri.equals("/409")) {
			resp.setStatus(StatusCodes.CONFLICT);
		}
		else if(uri.equals("/415")) {
			resp.setStatus(StatusCodes.UNSUPPORTED_MEDIA_TYPE);
		}
		else if(uri.equals("/500")) {
			resp.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
		}
		else if(uri.equals("/501")) {
			resp.setStatus(501);
		}
		else {
			super.doPost(req, resp);
		}
	}
}
