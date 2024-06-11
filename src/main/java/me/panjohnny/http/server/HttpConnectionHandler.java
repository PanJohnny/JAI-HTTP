package me.panjohnny.http.server;

import me.panjohnny.http.server.impl.HttpConnectionHandlerImpl;
import me.panjohnny.http.server.route.Router;

import java.io.IOException;
import java.net.Socket;

public abstract sealed class HttpConnectionHandler permits HttpConnectionHandlerImpl {
    protected final Socket socket;
    protected final Router router;

    public HttpConnectionHandler(Socket socket, Router router) {
        this.socket = socket;
        this.router = router;
    }

    public abstract void handle() throws IOException;

    public synchronized void close() throws IOException {
        socket.close();
    }
}
