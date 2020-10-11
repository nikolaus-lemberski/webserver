package com.lemberski.webserver.http.response;

import lombok.Data;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Data
public class Response {

    private String httpVersion;
    private Status status;
    private Path filePath;
    private Map<String, String> headers = new HashMap<>();

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

}
