import me.panjohnny.http.server.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import static me.panjohnny.http.fetch.SimpleFetch.fetch;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpServerTests {
    private static HttpServer server;

    @BeforeAll
    public static void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 21902));

        var router = server.getRouter();

        router.route("/static", (req, res) -> {
            res.html();
            res.body().setBody("<h1>Hello, World!</h1>");
        });

        router.route("/dynamic/[param]", (req, res) -> {
            res.html();
            assert req.parameters() != null;
            res.body().setBody("<h1>Hello, " + req.parameters().getString("param") + "!</h1>");
        });

        router.route("/dynamic/[param]/[param2]", (req, res) -> {
            res.html();
            assert req.parameters() != null;
            res.body().setBody("<h1>Hello, " + req.parameters().getString("param") + " " + req.parameters().getString("param2") + "!</h1>");
        });

        server.start();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server.stop();
    }

    @Test
    void staticRoute() throws ExecutionException, InterruptedException {
        var response = fetch("http://localhost:21902/static", null).get();
        assertEquals(200, response.statusCode);
        assertEquals("<h1>Hello, World!</h1>", response.body().toString());
    }

    @Test
    void dynamicRoute() throws ExecutionException, InterruptedException {
        var response = fetch("http://localhost:21902/dynamic/John", null).get();
        assertEquals(200, response.statusCode);
        assertEquals("<h1>Hello, John!</h1>", response.body().toString());
    }

    @Test
    void dynamicRouteWithTwoParams() throws ExecutionException, InterruptedException {
        var response = fetch("http://localhost:21902/dynamic/John/Doe", null).get();
        assertEquals(200, response.statusCode);
        assertEquals("<h1>Hello, John Doe!</h1>", response.body().toString());
    }
}
