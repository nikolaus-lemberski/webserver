package com.lemberski.webserver.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.lemberski.webserver.http.Constants.*;

@Service
public class RequestParser {

    private static final Logger LOG = LoggerFactory.getLogger(RequestParser.class);

    public Request from(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        Request request = new Request();
        boolean requestLine = true;
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            if (requestLine) {
                readRequestLine(request, line);
                requestLine = false;
            } else {
                readHeader(request, line);
            }
        }

        LOG.debug("Received request {}", request);
        return request;
    }

    private void readRequestLine(Request request, String line) {
        String[] values = line.split(BLANK);
        request.setMethod(Method.valueOf(values[0]));
        request.setPath(values[1]);
        request.setHttpVersion(values[2]);
    }

    private void readHeader(Request request, String line) {
        if (line.startsWith(CONNECTION)) {
            String[] values = line.split(HEADER_SEPARATOR);
            request.setKeepAlive(values[1]);
        }
    }

}
