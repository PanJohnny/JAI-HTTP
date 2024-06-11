package me.panjohnny.http.server.route;

import me.panjohnny.http.commons.URLUtil;
import me.panjohnny.http.commons.request.Parameters;

import java.util.HashMap;

/**
 * Dynamic route, enables matching the path dynamically.
 * <p>
 * <h2>Dynamic path matching</h2>
 * - Routes with [] are dynamic routes.
 * - For example, if the path is "/home/[id]", it will match "/home/1", "/home/2", etc.
 * - You can nest dynamic routes, for example, "/home/[id]/[action]" will match "/home/1/edit", "/home/2/delete", etc.
 * @see StaticRoute
 */
public final class DynamicRoute extends Route {
    public DynamicRoute(String path) {
        super(URLUtil.isolateResource(path));
    }

    @Override
    public boolean matches(String path) {
        String[] pathParts = URLUtil.isolateResource(path).split("/");
        String[] routeParts = this.path.split("/");
        if (pathParts.length != routeParts.length) {
            return false;
        }
        for (int i = 0; i < pathParts.length; i++) {
            if (routeParts[i].startsWith("[") && routeParts[i].endsWith("]")) {
                continue;
            }
            if (!pathParts[i].equals(routeParts[i])) {
                return false;
            }
        }
        return true;
    }

    public Parameters parseParameters(String path) {
        String[] pathParts = URLUtil.isolateResource(path).split("/");
        String[] routeParts = this.path.split("/");
        HashMap<String, String> parameters = new HashMap<>();
        for (int i = 0; i < pathParts.length; i++) {
            if (routeParts[i].startsWith("[") && routeParts[i].endsWith("]")) {
                parameters.put(routeParts[i].substring(1, routeParts[i].length() - 1), pathParts[i]);
            }
        }

        return new Parameters(parameters);
    }
}
