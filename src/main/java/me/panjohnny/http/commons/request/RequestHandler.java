package me.panjohnny.http.commons.request;

import me.panjohnny.http.commons.response.Response;

public interface RequestHandler {
    void handle(Request request, Response response);
}
