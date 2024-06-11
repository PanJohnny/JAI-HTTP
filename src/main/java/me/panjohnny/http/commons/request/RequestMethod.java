package me.panjohnny.http.commons.request;

public enum RequestMethod {
    /**
     * The GET method requests a representation of the specified resource. Requests using GET should only retrieve data.
     */
    GET,
    /**
     * The HEAD method asks for a response identical to a GET request, but without the response body.
     */
    HEAD,
    /**
     * The POST method submits an entity to the specified resource, often causing a change in state or side effects on the server.
     */
    POST,
    /**
     * The PUT method replaces all current representations of the target resource with the request payload.
     */
    PUT,

    /**
     * The DELETE method deletes the specified resource.
     */
    DELETE,
    /**
     * The CONNECT method establishes a tunnel to the server identified by the target resource.
     * OPTIONS,
     */
    CONNECT,
    /**
     * The OPTIONS method describes the communication options for the target resource.
     */
    OPTIONS,
    /**
     * The TRACE method performs a message loop-back test along the path to the target resource.
     */
    TRACE,
    /**
     * The PATCH method applies partial modifications to a resource.
     */
    PATCH;

    public static final RequestMethod[] BODY_USABLE = new RequestMethod[]{
            POST,
            PUT,
            PATCH,
            DELETE
    };

    public static boolean isBodyUsable(RequestMethod method) {
        for (RequestMethod usable : BODY_USABLE) {
            if (usable == method) {
                return true;
            }
        }
        return false;
    }
}
