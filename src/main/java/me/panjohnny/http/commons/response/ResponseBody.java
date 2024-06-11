package me.panjohnny.http.commons.response;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class ResponseBody {
    private byte[] body;

    public ResponseBody(byte[] body) {
        this.body = body;
    }

    public ResponseBody(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public ResponseBody() {
        this.body = new byte[0];
    }

    @Override
    public String toString() {
        return new String(body);
    }

    public void setBody(String body) {
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void append(String str) {
        // Combine the two arrays
        byte[] newBody = new byte[body.length + str.getBytes(StandardCharsets.UTF_8).length];
        System.arraycopy(body, 0, newBody, 0, body.length);
        System.arraycopy(str.getBytes(StandardCharsets.UTF_8), 0, newBody, body.length, str.getBytes(StandardCharsets.UTF_8).length);
        this.body = newBody;
    }

    public void readFile(String path) throws IOException {
        this.body = Files.readAllBytes(Paths.get(path));
    }

    public void readStream(InputStream stream) throws IOException {
        byte[] buffer = new byte[stream.available()];
        if (stream.read(buffer) == 0) {
            System.getLogger(this.getClass().getName()).log(System.Logger.Level.WARNING, "Stream is empty");
        }

        this.body = buffer;
    }

    public byte[] blob() {
        return body;
    }
}
