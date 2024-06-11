package me.panjohnny.http.server;

import me.panjohnny.http.server.impl.HttpServerImpl;
import me.panjohnny.http.server.route.Router;

import java.io.IOException;
import java.net.InetSocketAddress;

public abstract sealed class HttpServer permits HttpServerImpl {
    protected final InetSocketAddress address;
    protected final int threadPoolSize;
    public HttpServer(InetSocketAddress address) {
        this.address = address;
        this.threadPoolSize = 10;
    }

    public HttpServer(InetSocketAddress address, int threadPoolSize) {
        this.address = address;
        this.threadPoolSize = threadPoolSize;
    }

    public abstract void start() throws IOException;
    public abstract void stop() throws InterruptedException;

    public abstract Router getRouter();

    public static HttpServer create(InetSocketAddress address) {
        return new HttpServerImpl(address);
    }

    public static HttpServer create(InetSocketAddress address, int threadPoolSize) {
        return new HttpServerImpl(address, threadPoolSize);
    }
}
