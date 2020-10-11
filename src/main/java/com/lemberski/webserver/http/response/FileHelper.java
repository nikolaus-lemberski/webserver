package com.lemberski.webserver.http.response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.String.format;

@Service
public class FileHelper {

    private static final Logger LOG = LoggerFactory.getLogger(FileHelper.class);

    @Value("${www.root.dir}")
    private String rootDir;

    private Path rootDirPath;

    @PostConstruct
    private void init() {
        initRootDirPath(rootDir);
    }

    public Optional<Path> toFullPath(String path) {
        if (path.contains("./") || path.contains("~/")) {
            LOG.warn("Trying to access illegal path, blocking. Path: {}", path);
            return Optional.empty();
        }

        Path fullPath = Paths.get(rootDirPath.toString(), path);
        return Files.exists(fullPath) ? Optional.of(fullPath) : Optional.empty();
    }

    public MimeType mimeType(String path) {
        String fileEnding = path.substring(path.lastIndexOf('.') + 1);
        return MimeType.forFileEnding(fileEnding);
    }

    private void initRootDirPath(String rootDir) {
        rootDirPath = Paths.get(rootDir);
        if (Files.notExists(rootDirPath)) {
            throw new RuntimeException(format("Error in 'www.root.dir' configuration '%s'", rootDir));
        }

        LOG.info("Root directory for files is '{}'", rootDirPath);
    }

}
