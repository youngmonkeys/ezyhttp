package com.tvd12.ezyhttp.client.test.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.io.EzyStrings;
import com.tvd12.ezyhttp.core.codec.JsonBodyConverter;
import com.tvd12.ezyhttp.core.constant.ContentTypes;
import com.tvd12.ezyhttp.core.constant.StatusCodes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TestBlockingServlet extends HttpServlet {
    private static final long serialVersionUID = 1321397014207226911L;

    private final ObjectMapper objectMapper
        = new ObjectMapper();
    private final JsonBodyConverter jsonBodyConverter
        = new JsonBodyConverter(objectMapper);

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
        switch (uri) {
            case "/greet": {
                String who = req.getParameter("who");
                if (who == null && !req.getMethod().equalsIgnoreCase("GET")) {
                    who = (String) jsonBodyConverter.deserialize(req.getInputStream(), Map.class)
                        .get("who");
                }

                if (EzyStrings.isNoContent(who)) {
                    resp.getOutputStream().write("bad request".getBytes());
                    resp.setStatus(StatusCodes.BAD_REQUEST);
                    return;
                }

                String message = "{\"message\":\"Greet " + who + "!\"}";
                resp.setContentType(ContentTypes.APPLICATION_JSON);
                resp.getOutputStream().write(message.getBytes());
                resp.setStatus(StatusCodes.OK);
                break;
            }
            case "/form": {
                String who = req.getParameter("who");
                if (EzyStrings.isNoContent(who)) {
                    resp.getOutputStream().write("bad request".getBytes());
                    resp.setStatus(StatusCodes.BAD_REQUEST);
                    return;
                }

                String message = "{\"message\":\"Greet " + who + "!\"}";
                resp.getOutputStream().write(message.getBytes());
                resp.setStatus(StatusCodes.OK);
                break;
            }
            case "/upload": {
                String uploadDir = "uploaded-files";
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    if (!uploadDirFile.mkdirs()) {
                        throw new IllegalStateException("can not create directory");
                    }
                }
                Part filePart = req.getPart("file");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = filePart.getSubmittedFileName();
                    File file = new File(uploadDir, fileName);
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            throw new IllegalStateException("can not create file");
                        }
                    }
                    try (InputStream inputStream = filePart.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    resp.getWriter().write("{\"ok\": true}");
                } else {
                    resp.getWriter().write("{\"ok\": false}");
                }
                break;
            }
            case "/no-content-error":
                resp.setContentLength(1);
                resp.getOutputStream().write(new byte[]{0});
                resp.setStatus(StatusCodes.OK);
                break;
            case "/401":
                resp.setStatus(StatusCodes.UNAUTHORIZED);
                break;
            case "/402":
                resp.setStatus(StatusCodes.PAYMENT_REQUIRED);
                break;
            case "/403":
                resp.setStatus(StatusCodes.FORBIDDEN);
                break;
            case "/404":
                resp.setStatus(StatusCodes.NOT_FOUND);
                break;
            case "/405":
                resp.setStatus(StatusCodes.METHOD_NOT_ALLOWED);
                break;
            case "/406":
                resp.setStatus(StatusCodes.NOT_ACCEPTABLE);
                break;
            case "/408":
                resp.setStatus(StatusCodes.REQUEST_TIMEOUT);
                break;
            case "/409":
                resp.setStatus(StatusCodes.CONFLICT);
                break;
            case "/415":
                resp.setStatus(StatusCodes.UNSUPPORTED_MEDIA_TYPE);
                break;
            case "/429":
                resp.setStatus(StatusCodes.TOO_MANY_REQUESTS);
                break;
            case "/500":
                resp.setStatus(StatusCodes.INTERNAL_SERVER_ERROR);
                break;
            case "/501":
                resp.setStatus(501);
                break;
            default:
                super.doPost(req, resp);
                break;
        }
    }
}
