package me.panjohnny.http.fetch;

import me.panjohnny.http.commons.Headers;
import me.panjohnny.http.commons.request.RequestMethod;

public record RequestData(RequestMethod method, Headers headers, String body) {
    public RequestData {
        if (method == null) {
            method = RequestMethod.GET;
        }
        if (headers == null) {
            headers = new Headers();
        }
        if (body == null) {
            body = "";
        }
    }

    public static RequestData defaultData() {
        return new RequestData(null, null, null);
    }
}
