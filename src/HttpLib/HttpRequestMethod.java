package HttpLib;

import java.util.EnumSet;
import java.util.HashMap;

public enum HttpRequestMethod {

    // Supported for assignment
    POST, GET;

    private static final HashMap<String, HttpRequestMethod> lookup = new HashMap<String, HttpRequestMethod>();

    static {
        for (HttpRequestMethod s : EnumSet.allOf(HttpRequestMethod.class))
            lookup.put(s.toString().toUpperCase(), s);
    }

    @Override
    public String toString() {
        return this.name();
    }

    public static HttpRequestMethod get(String methodString) {
        return lookup.get(methodString.toUpperCase());
    }
}
