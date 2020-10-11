package com.lemberski.webserver.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Map;

import static com.lemberski.webserver.http.Constants.CRLF;
import static com.lemberski.webserver.http.Constants.HEADER_SEPARATOR;
import static java.lang.String.format;

@Service
public class ResponseWriter {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseWriter.class);

    public void send(Response response, OutputStream outputStream) {
        try {
            sendResponseLine(response, outputStream);
            sendHeaders(response, outputStream);
            sendContent(response, outputStream);
        } catch (IOException e) {
            LOG.warn("Error writing to OutputStream: {}", e.getMessage());
        } finally {
            try {
                outputStream.flush();
                LOG.debug("Response for {} flushed",
                        Status.OK.equals(response.getStatus())
                                ? response.getFilePath()
                                : response.getStatus());
            } catch (IOException e) {
                LOG.warn("Cannot flush to OutputStream: {}", e.getMessage());
            }
        }
    }

    private void sendResponseLine(Response response, OutputStream outputStream) throws IOException {
        outputStream.write(format("%s %s %s%s",
                response.getHttpVersion(),
                response.getStatus().getCode(),
                response.getStatus().getText(),
                CRLF)
                .getBytes());
    }

    private void sendHeaders(Response response, OutputStream outputStream) throws IOException {
        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            outputStream.write(header(entry.getKey(), entry.getValue()));
        }
        outputStream.write(CRLF.getBytes());
    }

    private void sendContent(Response response, OutputStream outputStream) throws IOException {
        if (Status.OK.equals(response.getStatus()) && response.getFilePath() != null) {
            outputStream.write(Files.readAllBytes(response.getFilePath()));
        }
    }

    private byte[] header(String key, Object value) {
        return format("%s%s%s%s", key, HEADER_SEPARATOR, value, CRLF).getBytes();
    }

}
