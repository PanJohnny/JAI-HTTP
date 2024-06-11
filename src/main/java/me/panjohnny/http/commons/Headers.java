package me.panjohnny.http.commons;

import java.util.HashMap;

public final class Headers {
    private final HashMap<String, String> headers;

    public Headers() {
        headers = new HashMap<>();
    }

    public Headers set(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public String get(String key) {
        return headers.get(key);
    }

    public void remove(String key) {
        headers.remove(key);
    }

    public boolean isSet(String key) {
        return headers.containsKey(key);
    }

    public HashMap<String, String> map() {
        return headers;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String key : headers.keySet()) {
            builder.append(key).append(": ").append(headers.get(key)).append("\r\n");
        }
        return builder.toString();
    }

    public void copyAll(Headers headers) {
        this.headers.putAll(headers.map());
    }
}
