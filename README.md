# JAI HTTP
[![Maven Package](https://github.com/PanJohnny/JAI-HTTP/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/PanJohnny/JAI-HTTP/actions/workflows/maven-publish.yml)

JAI HTTP or Just Another Implementation of HTTP is a simple library coded in Java, that consists of a server with routing
and an utility for easily fetching remote resources.

It currently supports HTTP 1.0 and 1.1.

Supports loading from files, to test it clone the repository and run `mvn clean install`. Test using JUnit 5.

## HttpServer

HttpServer is created with the static function create:

```java
HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8080));
```

After creating the server, you would need to add routes to it. Although it is possible, it is recommended to add all
the routes before starting the server.

### Routing

Routing is done by the Router, retrieved by the getRouter() method of the server.

```java
Router router = server.getRouter();
```

You can use the route method to add a route to the router:

```java
router.route("/",(req, res) ->{
        res.text("Hello, World!");
});
```

#### Static routing

The concept of static routing is what you would expect, it is a route that is always executed when the path matches the
request.

```java
router.staticRoute("/hello",(req, res) ->{
        res.text("Hello, World!");
});
```

#### Dynamic routing

Dynamic routing is a route that can have parameters in the path, for example:

```java
router.route("/hello/[name]",(req, res) ->{
        res.headers().set("Content-Type","text/plain");
        assert req.parameters() !=null;
        res.body().setBody("Hello, "+req.parameters().getString("name") +"!");
});
```

### Starting or stopping the server
The server can be started or stopped with the start() and stop() methods.

```java
server.start();
server.stop();
```

## fetch (SimpleFetch)
Similarly to JavaScript's fetch, SimpleFetch is a utility for fetching remote resources.

First (but optionally) import statically the fetch method:
```java
import static me.panjohnny.http.fetch.SimpleFetch.fetch;
```

Then you can use the fetch method to fetch resources:

```java
Response apiRes = fetch("https://cataas.com/cat", null).get(); // Second parameter is optional
// Same as:
Response apiRes = fetch("https://cataas.com/cat", new RequestData(RequestMethod.GET, new Headers(), new RequestBody())).get();
```
