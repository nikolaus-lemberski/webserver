package com.lemberski.webserver;

import com.lemberski.webserver.http.request.Method;
import com.lemberski.webserver.http.request.Request;
import com.lemberski.webserver.http.request.RequestParser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.lemberski.webserver.http.Constants.*;
import static org.junit.Assert.*;

public class RequestParserTest extends ContextAwareBaseTest {

    @Autowired
    private RequestParser requestParser;

    @Test
    public void testParseRequest() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET / HTTP/1.1" + CRLF);
        sb.append("Host: localhost:8080" + CRLF);
        sb.append("Connection: keep-alive" + CRLF);
        sb.append(CRLF);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Request request = requestParser.from(inputStream);

        assertEquals(request.getMethod(), Method.GET);
        assertEquals(request.getHttpVersion(), HTTP_1_1);
        assertEquals(request.getPath(), SLASH);
        assertTrue(request.isKeepAlive());
        assertEquals(request.getKeepAlive(), KEEP_ALIVE_VALUE);

        inputStream.close();
    }

    @Test
    public void testParseRequestAbsoluteUriWithSlash() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET http://localhost:8080/ HTTP/1.1" + CRLF);
        sb.append(CRLF);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Request request = requestParser.from(inputStream);

        assertEquals(request.getPath(), SLASH);

        inputStream.close();
    }

    @Test
    public void testParseRequestAbsoluteUriWithoutSlash() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET http://localhost:8080 HTTP/1.1" + CRLF);
        sb.append(CRLF);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Request request = requestParser.from(inputStream);

        assertEquals(request.getPath(), SLASH);

        inputStream.close();
    }

    @Test
    public void testParseRequestSubdirPath() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET http://localhost:8080/css/styles.css HTTP/1.1" + CRLF);
        sb.append(CRLF);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Request request = requestParser.from(inputStream);

        assertEquals(request.getPath(), "/css/styles.css");

        inputStream.close();
    }

    @Test
    public void testConnectionClose() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET / HTTP/1.1" + CRLF);
        sb.append("Host: localhost:8080" + CRLF);
        sb.append("Connection: close" + CRLF);
        sb.append(CRLF);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Request request = requestParser.from(inputStream);

        assertEquals(request.getHttpVersion(), HTTP_1_1);
        assertFalse(request.isKeepAlive());
        assertEquals(request.getKeepAlive(), CLOSE_VALUE);

        inputStream.close();
    }
    @Test
    public void testConnectionKeepAlive() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET / HTTP/1.0" + CRLF);
        sb.append("Host: localhost:8080" + CRLF);
        sb.append("Connection: keep-alive" + CRLF);
        sb.append(CRLF);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Request request = requestParser.from(inputStream);

        assertNotEquals(request.getHttpVersion(), HTTP_1_1);
        assertTrue(request.isKeepAlive());
        assertEquals(request.getKeepAlive(), KEEP_ALIVE_VALUE);

        inputStream.close();
    }

    @Test
    public void testConnectionKeepAliveDefault() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("GET / HTTP/1.1" + CRLF);
        sb.append("Host: localhost:8080" + CRLF);
        sb.append(CRLF);

        InputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());
        Request request = requestParser.from(inputStream);

        assertEquals(request.getHttpVersion(), HTTP_1_1);
        assertTrue(request.isKeepAlive());

        inputStream.close();
    }

}
