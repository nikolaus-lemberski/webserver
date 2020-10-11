package com.lemberski.webserver;

import com.lemberski.webserver.http.ClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Provider;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class Server {

    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    private ServerSocket serverSocket;

    @Autowired
    private Provider<ClientHandler> clientHandlerProvider;

    @Value("${port}")
    private int port;

    @PostConstruct
    private void init() {
        LOG.info("Initializing server on port {}", port);
        try {
            serverSocket.bind(new InetSocketAddress(port));
            LOG.info("Server initialized on port {}", serverSocket.getLocalPort());
        } catch (IOException e) {
            throw new RuntimeException("Error creating the ServerSocket", e);
        }
    }

    @PreDestroy
    private void shutdown() {
        LOG.info("Shutting down the server");

        try {
            serverSocket.close();
        } catch (IOException e) {
            LOG.warn("Cannot close the ServerSocket", e);
        }

        LOG.info("Server closed");
    }

    @Async
    public void start() {
        while (serverSocket.isBound() && !serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                taskExecutor.execute(clientHandlerProvider.get().with(clientSocket));
            } catch (IOException e) {
                LOG.warn("I/O Error handling Client-Connection", e);
            }
        }
        LOG.error("Server stopped");
    }

}
