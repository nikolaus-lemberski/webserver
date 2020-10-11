package com.lemberski.webserver.http;

import com.lemberski.webserver.http.request.Request;
import com.lemberski.webserver.http.request.RequestParser;
import com.lemberski.webserver.http.response.Response;
import com.lemberski.webserver.http.response.ResponseBuilder;
import com.lemberski.webserver.http.response.ResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static java.util.Objects.requireNonNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ClientHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ClientHandler.class);

    @Autowired
    private RequestParser requestParser;

    @Autowired
    private ResponseBuilder responseBuilder;

    @Autowired
    private ResponseWriter responseWriter;

    @Value("${connection.timeout.sec}")
    private int connectionTimeout;

    private Socket clientSocket;

    public ClientHandler with(Socket clientSocket) throws SocketException {
        this.clientSocket = clientSocket;
        clientSocket.setKeepAlive(true);
        clientSocket.setSoTimeout(connectionTimeout * 1000);
        return this;
    }

    @Override
    public void run() {
        requireNonNull(clientSocket);

        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {
            boolean keepAlive = true;
            while (keepAlive) {
                try {
                    Request request = requestParser.from(inputStream);

                    keepAlive = request.isKeepAlive();
                    clientSocket.setKeepAlive(keepAlive);

                    Response response = responseBuilder.from(request);
                    responseWriter.send(response, outputStream);
                } catch (SocketTimeoutException e) {
                    LOG.debug("Client connection timeout, 'connection.timeout.sec' configured to {}.", connectionTimeout);
                    keepAlive = false;
                } catch (Exception e) {
                    LOG.warn("Error reading request from client, {}", e.getMessage());
                    keepAlive = false;
                }
            }
        } catch (IOException e) {
            LOG.warn("I/O error in connection with client", e);
        } finally {
            closeClientSocket();
        }
    }

    private void closeClientSocket() {
        if (!clientSocket.isClosed()) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                LOG.warn("Cannot close clientSocket: {}", e.getMessage(), e);
            }
        }
    }

}
