package me.panjohnny.http;

import me.panjohnny.http.commons.response.Response;
import me.panjohnny.http.server.HttpServer;
import me.panjohnny.http.server.route.Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import static me.panjohnny.http.fetch.SimpleFetch.fetch;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080));
        Router router = server.getRouter();

        router.route("/", (req, res) -> {
            res.html();
            try {
                res.body().readFile("./hello.html");
            } catch (IOException e) {
                res.statusCode = 500;
                res.statusMessage = "Internal Server Error";
            }
        });

        router.route("/hello/[name]", (req, res) -> {
            res.headers().set("Content-Type", "text/plain");
            assert req.parameters() != null;
            res.body().setBody("Hello, " + req.parameters().getString("name") + "!");
        });

        router.route("/hello/[name]/[surname]", (req, res) -> {
            res.headers().set("Content-Type", "text/plain");
            assert req.parameters() != null;
            res.body().setBody("Hello, " + req.parameters().getString("name") + " " + req.parameters().getString("surname") + "!");
        });

        router.route("/cat", (req, res) -> {
            try {
                Response apiRes = fetch("https://cataas.com/cat", null).get();

                res.headers().copyAll(apiRes.headers());
                res.headers().remove("Set-Cookie");
                res.headers().remove("Cookie");
                res.body().setBody(apiRes.body().blob());
            } catch (InterruptedException | ExecutionException e) {
                res.statusCode = 500;
                res.statusMessage = "Internal Server Error";
            }
        });

        server.start();
    }
}