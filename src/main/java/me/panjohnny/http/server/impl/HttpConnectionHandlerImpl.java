package me.panjohnny.http.server.impl;

import me.panjohnny.http.commons.Headers;
import me.panjohnny.http.commons.HttpVersion;
import me.panjohnny.http.commons.request.Parameters;
import me.panjohnny.http.commons.request.Request;
import me.panjohnny.http.commons.request.RequestBody;
import me.panjohnny.http.commons.request.RequestMethod;
import me.panjohnny.http.server.HttpConnectionHandler;
import me.panjohnny.http.commons.response.Response;
import me.panjohnny.http.server.route.DynamicRoute;
import me.panjohnny.http.server.route.Route;
import me.panjohnny.http.server.route.Router;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public final class HttpConnectionHandlerImpl extends HttpConnectionHandler {
    private final System.Logger logger;
    public HttpConnectionHandlerImpl(Socket socket, Router router) {
        super(socket, router);
        logger = System.getLogger(HttpConnectionHandlerImpl.class.getName());
    }

    @Override
    @SuppressWarnings("UnusedReturnValue")
    public void handle() throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {

            // Read the request line
            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            // Split the request line into parts
            String[] requestParts = requestLine.split(" ");
            if (requestParts.length != 3) {
                return; // Invalid request line
            }

            RequestMethod method;
            try {
                method = RequestMethod.valueOf(requestParts[0]);
            } catch (IllegalArgumentException e) {
                send(new Response(HttpVersion.HTTP_1_1, 405, "Method Not Allowed"));
                return; // Invalid method
            }

            String resource = requestParts[1];

            if (!router.hasRoute(resource)) {
                send(new Response(HttpVersion.HTTP_1_1, 404, "Not Found"));
                return; // Resource not found
            }

            HttpVersion httpVersion = HttpVersion.fromString(requestParts[2]);

            if (httpVersion == null) {
                send(new Response(HttpVersion.HTTP_1_1, 505, "HTTP Version Not Supported"));
                return; // Invalid HTTP version
            }

            // Read headers
            String headerLine;
            Headers headers = new Headers();

            while (!(headerLine = in.readLine()).isEmpty()) {
                String[] headerParts = headerLine.split(": ");
                headers.set(headerParts[0], headerParts[1]);
            }

            // Handle different HTTP methods
            RequestBody requestBody = null;
            if (RequestMethod.isBodyUsable(method)) {
                // Read the body if present
                StringBuilder body = new StringBuilder();
                if (headers.isSet("Content-Length")) {
                    int contentLength = Integer.parseInt(headers.get("Content-Length"));
                    char[] bodyChars = new char[contentLength];
                    int totalRead = 0;
                    while (totalRead < contentLength) {
                        int bytesRead = in.read(bodyChars, totalRead, contentLength - totalRead);
                        if (bytesRead == -1) { // End of stream
                            break;
                        }
                        totalRead += bytesRead;
                    }
                    body.append(bodyChars, 0, totalRead);
                }

                requestBody = new RequestBody(body.toString());
            }

            // Get the query parameters
            Parameters query = getParameters(resource);

            // Get fragment
            String fragment = null;
            String[] resourceParts = resource.split("#");
            if (resourceParts.length > 1) {
                resource = resourceParts[0];
                fragment = resourceParts[1];
            }

            Route route = router.getRoute(resource);
            Parameters dynamicParameters = null;
            if (route instanceof DynamicRoute dyn) {
                dynamicParameters = dyn.parseParameters(resource);
            }

            Request request = new Request(resource, method, httpVersion, headers, requestBody, query, fragment, dynamicParameters);

            Response response = new Response(HttpVersion.HTTP_1_1, 200, "OK");

            router.getHandler(resource).handle(request, response);

            send(response);
        } catch (IOException e) {
            logger.log(System.Logger.Level.WARNING, "An error occurred while handling the connection", e);
        }
    }

    private @Nullable Parameters getParameters(String resource) {
        Parameters query = null;
        String[] resourceParts = resource.split("\\?");
        if (resourceParts.length > 1) {
            String[] queryParts = resourceParts[1].split("&");
            HashMap<String, String> queryMap = new HashMap<>();
            for (String queryPart : queryParts) {
                String[] queryParam = queryPart.split("=");

                String value = queryParam[1];
                if (value.contains("#")) {
                    value = value.split("#")[0];
                    queryMap.put(queryParam[0], value);
                    break;
                }

                queryMap.put(queryParam[0], value);
            }
            query = new Parameters(queryMap);
        }
        return query;
    }

    private void send(Response response) {
        try {
            OutputStream out = socket.getOutputStream();
            out.write(response.toBytes());
            out.flush();
        } catch (IOException e) {
            logger.log(System.Logger.Level.WARNING, "An error occurred while sending the response", e);
        }
    }
}
