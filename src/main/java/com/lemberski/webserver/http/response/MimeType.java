package com.lemberski.webserver.http.response;

import lombok.Getter;

@Getter
public enum MimeType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_SVG_XML("image/svg+xml"),
    APPLICATION_OCTET_STREAM("application/octet-stream");

    private final String text;

    MimeType(String text) {
        this.text = text;
    }
}
