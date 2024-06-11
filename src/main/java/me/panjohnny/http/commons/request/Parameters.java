package me.panjohnny.http.commons.request;

import java.util.HashMap;

public class Parameters {
    private final HashMap<String, String> parameters;
    public Parameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getString(String key) {
        return parameters.get(key);
    }

    public int getInt(String key) {
        return Integer.parseInt(parameters.get(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(parameters.get(key));
    }

    public boolean isSet(String key) {
        return parameters.containsKey(key);
    }
}
