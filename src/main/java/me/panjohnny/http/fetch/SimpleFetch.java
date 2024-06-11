package me.panjohnny.http.fetch;

import me.panjohnny.http.commons.Headers;
import me.panjohnny.http.commons.HttpVersion;
import me.panjohnny.http.commons.request.Request;
import me.panjohnny.http.commons.request.RequestMethod;
import me.panjohnny.http.commons.response.Response;
import me.panjohnny.http.commons.response.ResponseBody;
import org.jetbrains.annotations.Nullable;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public final class SimpleFetch {
    public static CompletableFuture<Response> fetch(String url, @Nullable RequestData data) {
        if (data == null) {
            data = RequestData.defaultData();
        }
        RequestData finalData = data;
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Use built-in Java HTTP client to fetch the data
                URL url_ = new URI(url).toURL();
                HttpURLConnection con = (HttpURLConnection) url_.openConnection();
                con.setRequestMethod(finalData.method().name());

                finalData.headers().map().forEach(con::setRequestProperty);

                // Set body
                if (finalData.body() != null && !finalData.body().isEmpty()) {
                    if (!RequestMethod.isBodyUsable(finalData.method())) {
                        con.disconnect();
                        throw new IllegalArgumentException("Method " + finalData.method() + " does not support body");
                    }

                    con.setDoOutput(true);
                    con.getOutputStream().write(finalData.body().getBytes());
                }

                // Finish and send
                con.connect();

                // Get response
                int responseCode = con.getResponseCode();
                String responseMessage = con.getResponseMessage();
                byte[] responseBody = con.getInputStream().readAllBytes();

                // Get http version
                String version = con.getHeaderField(0);
                String[] parts = version.split(" ");
                HttpVersion httpVersion = HttpVersion.fromString(parts[0]);

                // Read all headers
                Headers headers = new Headers();
                var headerFields = con.getHeaderFields();
                headerFields.forEach((k, v) -> {
                    if (k != null) {
                        headers.set(k, v.getFirst());
                    }
                });

                con.disconnect();

                return new Response(httpVersion, responseCode, responseMessage, headers, new ResponseBody(responseBody));
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }
}
