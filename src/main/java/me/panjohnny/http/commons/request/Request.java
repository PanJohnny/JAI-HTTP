package me.panjohnny.http.commons.request;

import me.panjohnny.http.commons.Headers;
import me.panjohnny.http.commons.HttpVersion;
import org.jetbrains.annotations.Nullable;

public record Request(String resource, RequestMethod method, HttpVersion version, Headers headers, @Nullable RequestBody body, @Nullable Parameters query, @Nullable String fragment, @Nullable Parameters parameters) {
}
