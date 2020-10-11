package com.lemberski.webserver;

import com.lemberski.webserver.http.request.Request;
import com.lemberski.webserver.http.response.Response;
import com.lemberski.webserver.http.response.ResponseBuilder;
import com.lemberski.webserver.http.response.Status;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

import static com.lemberski.webserver.http.Constants.*;
import static com.lemberski.webserver.http.request.Method.GET;
import static com.lemberski.webserver.http.request.Method.POST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResponseBuilderTest extends ContextAwareBaseTest {

    @Autowired
    private ResponseBuilder responseBuilder;

    @Value("${www.root.page}")
    private String rootPage;

    @Value("${www.charset}")
    private String charset;

    @Test
    public void testBuildResponse() throws IOException {
        Request request = new Request();
        request.setMethod(GET);
        request.setHttpVersion(HTTP_1_1);
        request.setKeepAlive(KEEP_ALIVE_VALUE);
        request.setPath(SLASH);

        Response response = responseBuilder.from(request);

        assertEquals(response.getStatus(), Status.OK);
        assertTrue(response.getFilePath().endsWith(rootPage));
        assertEquals(response.getHttpVersion(), HTTP_1_1);
        assertTrue(response.getHeaders().containsKey(CONNECTION) && response.getHeaders().containsValue(KEEP_ALIVE_VALUE));
        assertTrue(response.getHeaders().containsKey(CONTENT_TYPE) && response.getHeaders().containsValue("text/html;charset=" + charset));
    }

    @Test
    public void testMethodNotAllowed() throws IOException {
        Request request = new Request();
        request.setMethod(POST);
        request.setHttpVersion(HTTP_1_1);
        request.setKeepAlive(KEEP_ALIVE_VALUE);
        request.setPath(SLASH);

        Response response = responseBuilder.from(request);

        assertEquals(response.getStatus(), Status.METHOD_NOT_ALLOWED);
        assertEquals(response.getHttpVersion(), HTTP_1_1);
        assertTrue(response.getHeaders().containsKey(CONNECTION) && response.getHeaders().containsValue(KEEP_ALIVE_VALUE));
    }

    @Test
    public void testNotFound() throws IOException {
        Request request = new Request();
        request.setMethod(GET);
        request.setHttpVersion(HTTP_1_1);
        request.setKeepAlive(KEEP_ALIVE_VALUE);
        request.setPath("nothing-here");

        Response response = responseBuilder.from(request);

        assertEquals(response.getStatus(), Status.NOT_FOUND);
        assertEquals(response.getHttpVersion(), HTTP_1_1);
        assertTrue(response.getHeaders().containsKey(CONNECTION) && response.getHeaders().containsValue(KEEP_ALIVE_VALUE));
    }

    @Test
    public void testInvalidRequest() throws IOException {
        Request request = new Request();
        request.setMethod(GET);

        Response response = responseBuilder.from(request);

        assertEquals(response.getStatus(), Status.INTERNAL_SERVER_ERROR);
    }

}
