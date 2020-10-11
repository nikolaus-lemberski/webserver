package com.lemberski.webserver.http.response;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lemberski.webserver.http.Constants.HTML_FILE_ENDING;
import static com.lemberski.webserver.http.Constants.HTM_FILE_ENDING;

@Getter
public enum MimeType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    TEXT_JAVASCRIPT("text/javascript"),
    IMAGE_GIF("image/gif"),
    IMAGE_JPG("image/jpeg"),
    IMAGE_PNG("image/png"),
    IMAGE_SVG_XML("image/svg+xml"),
    IMAGE_X_ICON("image/x-icon"),
    APPLICATION_OCTET_STREAM("application/octet-stream");

    private static final Logger LOG = LoggerFactory.getLogger(FileHelper.class);

    private final String text;

    MimeType(String text) {
        this.text = text;
    }

    static MimeType forFileEnding(String fileEnding) {
        switch (fileEnding) {
            case HTML_FILE_ENDING:
            case HTM_FILE_ENDING:
                return TEXT_HTML;
            case "css":
                return TEXT_CSS;
            case "js":
                return TEXT_JAVASCRIPT;
            case "gif":
                return IMAGE_GIF;
            case "jpg":
            case "jpeg":
                return IMAGE_JPG;
            case "png":
                return IMAGE_PNG;
            case "svg":
                return IMAGE_SVG_XML;
            case "ico":
                return IMAGE_X_ICON;
            default:
                LOG.warn("Unknown mime type for fileExtension {}", fileEnding);
                return APPLICATION_OCTET_STREAM;
        }
    }

}
