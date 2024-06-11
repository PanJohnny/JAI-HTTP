package me.panjohnny.http.commons.response;

import me.panjohnny.http.commons.Headers;
import me.panjohnny.http.commons.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class Response {
    private final HttpVersion version;
    public int statusCode;
    public String statusMessage;
    private final Headers headers;
    private final ResponseBody body;

    public Response(HttpVersion version, int statusCode, String statusMessage, Headers headers, ResponseBody body) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public Response(HttpVersion version, int statusCode, String statusMessage) {
        this(version, statusCode, statusMessage, new Headers(), new ResponseBody());
    }


    public void text(String text) {
        text();
        body.setBody(text);
    }

    public void html(String html) {
        html();
        body.setBody(html);
    }

    public void json(String json) {
        json();
        body.setBody(json);
    }

    public void text() {
        headers.set("Content-Type", "text/plain");
    }

    public void html() {
        headers.set("Content-Type", "text/html");
    }

    public void json() {
        headers.set("Content-Type", "application/json");
    }

    @Override
    public String toString() {
        return version.getVersion() + " " + statusCode + " " + statusMessage + "\r\n" + headers + "\r\n" + body;
    }

    public byte[] toBytes() {
        byte[] firstPart = (version.getVersion() + " " + statusCode + " " + statusMessage + "\r\n" + headers + "\r\n").getBytes(StandardCharsets.UTF_8);
        byte[] secondPart = body.blob();
        byte[] result = new byte[firstPart.length + secondPart.length];
        System.arraycopy(firstPart, 0, result, 0, firstPart.length);
        System.arraycopy(secondPart, 0, result, firstPart.length, secondPart.length);
        return result;
    }

    public HttpVersion version() {
        return version;
    }

    public Headers headers() {
        return headers;
    }

    public ResponseBody body() {
        return body;
    }

    public void setContentLength() {
        headers.set("Content-Length", String.valueOf(body.blob().length));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Response) obj;
        return Objects.equals(this.version, that.version) &&
                this.statusCode == that.statusCode &&
                Objects.equals(this.statusMessage, that.statusMessage) &&
                Objects.equals(this.headers, that.headers) &&
                Objects.equals(this.body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, statusCode, statusMessage, headers, body);
    }

}
