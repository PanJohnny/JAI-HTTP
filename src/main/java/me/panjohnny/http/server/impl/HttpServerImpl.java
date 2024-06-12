package me.panjohnny.http.server.impl;

import me.panjohnny.http.server.HttpServer;
import me.panjohnny.http.server.route.Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class HttpServerImpl extends HttpServer {
    private ServerSocket serverSocket;
    private Thread mainServerThread;
    private ExecutorService threadPool;
    private boolean running;
    private final System.Logger logger = System.getLogger(HttpServerImpl.class.getName());
    private final Router router = new Router();

    public HttpServerImpl(InetSocketAddress address) {
        super(address);
    }

    public HttpServerImpl(InetSocketAddress address, int threadPoolSize) {
        super(address, threadPoolSize);
    }

    @Override
    public void start() throws IOException {
        serverSocket = new ServerSocket();
        serverSocket.bind(address);

        threadPool = Executors.newFixedThreadPool(10);

        mainServerThread = new Thread(this::acceptConnections, "Main HTTP Server Thread");

        running = true;
        mainServerThread.start();

        logger.log(System.Logger.Level.INFO, "Server started on {0}", address);
    }

    @Override
    public void stop() throws InterruptedException {
        running = false;
        mainServerThread.join();

        threadPool.shutdownNow();

        logger.log(System.Logger.Level.INFO, "Server listening on {0} successfully stopped", address);
    }

    private void acceptConnections() {
        while (running) {
            threadPool.submit(() -> {
                try {
                    Socket socket = serverSocket.accept();
                    logger.log(System.Logger.Level.DEBUG, "Accepted connection from {0}", socket.getRemoteSocketAddress());

                    new HttpConnectionHandlerImpl(socket, getRouter()).handle();
                } catch (IOException e) {
                    if (!running)
                        return;

                    logger.log(System.Logger.Level.ERROR, "Error while handling connection", e);
                }
            });
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.log(System.Logger.Level.ERROR, "Error while closing server socket", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Router getRouter() {
        return router;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
