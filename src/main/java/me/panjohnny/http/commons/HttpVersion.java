package me.panjohnny.http.commons;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public static HttpVersion fromString(String version) {
        for (HttpVersion v : values()) {
            if (v.version.equals(version)) {
                return v;
            }
        }
        return null;
    }
}
