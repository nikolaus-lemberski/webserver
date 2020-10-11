package com.lemberski.webserver.http.request;

import lombok.Data;

import static com.lemberski.webserver.http.Constants.HTTP_1_1;
import static com.lemberski.webserver.http.Constants.KEEP_ALIVE_VALUE;

@Data
public class Request {

    private Method method;
    private String path;
    private String httpVersion;
    private String keepAlive;

    public boolean isKeepAlive() {
        if (keepAlive != null) {
            return KEEP_ALIVE_VALUE.equals(keepAlive);
        }

        return HTTP_1_1.equals(httpVersion);
    }

    public boolean isValid() {
        return method != null && path != null && httpVersion != null;
    }

}
