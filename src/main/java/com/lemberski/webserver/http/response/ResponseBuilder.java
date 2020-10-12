package com.lemberski.webserver.http.response;

import com.lemberski.webserver.http.request.Method;
import com.lemberski.webserver.http.request.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static com.lemberski.webserver.http.Constants.*;
import static com.lemberski.webserver.http.request.Method.GET;
import static com.lemberski.webserver.http.request.Method.HEAD;
import static java.lang.String.format;

@Service
public class ResponseBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseBuilder.class);

    @Autowired
    private FileHelper fileHelper;

    @Value("${www.root.page}")
    private String rootPage;

    @Value("${connection.timeout.sec}")
    private int connectionTimeout;

    @Value("${www.charset}")
    private String charset;

    public Response from(Request request) throws IOException {
        Response response = new Response();
        if (!request.isValid()) {
            LOG.error("Invalid request, sending internal server error, {}", request);
            setError(Status.INTERNAL_SERVER_ERROR, response);
            return response;
        }

        setHttpVersion(request.getHttpVersion(), response);
        setKeepAliveHeader(request.isKeepAlive(), response);

        switch (request.getMethod()) {
            case GET:
                setContent(request.getPath(), response);
                break;
            case HEAD:
                String path = request.getPath();
                path = addRootPageIfNecessary(path);
                if (fileHelper.toFullPath(path).isPresent()) {
                    response.setStatus(Status.OK);
                } else {
                    response.setStatus(Status.NOT_FOUND);
                }
                response.addHeader(CONTENT_LENGTH, String.valueOf(0));
                break;
            default:
                LOG.error("Method {} not supported, sending 405, {}", request.getMethod(), request);
                setError(Status.METHOD_NOT_ALLOWED, response);
                break;
        }

        return response;
    }

    private void setHttpVersion(String httpVersion, Response response) {
        response.setHttpVersion(httpVersion);
    }

    private void setKeepAliveHeader(boolean keepAlive, Response response) {
        if (keepAlive) {
            response.addHeader(CONNECTION, KEEP_ALIVE_VALUE);
            response.addHeader(KEEP_ALIVE, format("timeout=%s, max=1000", connectionTimeout));
        } else {
            response.addHeader(CONNECTION, CLOSE_VALUE);
        }
    }

    private void setContent(String path, Response response) throws IOException {
        path = addRootPageIfNecessary(path);
        Optional<Path> contentPath = fileHelper.toFullPath(path);
        if (contentPath.isPresent()) {
            response.setStatus(Status.OK);
            response.setFilePath(contentPath.get());

            MimeType mimeType = fileHelper.mimeType(path);
            if (needsCharset(mimeType)) {
                response.addHeader(CONTENT_TYPE, format("%s;charset=%s", mimeType.getText(), charset));
            } else {
                response.addHeader(CONTENT_TYPE, mimeType.getText());
            }
            response.addHeader(CONTENT_LENGTH, String.valueOf(Files.size(contentPath.get())));
        } else {
            LOG.warn("File for path '{}' not found, sending 404.", path);
            setError(Status.NOT_FOUND, response);
        }
    }

    private String addRootPageIfNecessary(String path) {
        if (path.endsWith(SLASH)) {
            path = path + rootPage;
        }
        return path;
    }

    private void setError(Status errorStatus, Response response) {
        response.setStatus(errorStatus);
        response.addHeader(CONTENT_LENGTH, String.valueOf(0));
    }

    private boolean isMethodSupported(Method method) {
        return isGetMethod(method) || isHeadMethod(method);
    }

    private boolean isGetMethod(Method method) {
        return GET.equals(method);
    }

    private boolean isHeadMethod(Method method) {
        return HEAD.equals(method);
    }

    private boolean needsCharset(MimeType mimeType) {
        return MimeType.TEXT_HTML.equals(mimeType)
                || MimeType.TEXT_CSS.equals(mimeType)
                || MimeType.TEXT_JAVASCRIPT.equals(mimeType);
    }

}
