package HttpLib;

import java.util.EnumSet;
import java.util.HashMap;

public enum HttpStatusCode {
    OK(200), Created(201), Accepted(202), NoContent(204),
    MovedPermanently(301), MovedTemporarily(302), NotModified(304),
    BadRequest(400), Unauthorized(401), Forbidden(403), NotFound(404),
    InternalServerError(500);

    private static final HashMap<Integer, HttpStatusCode> lookupCode = new HashMap<Integer, HttpStatusCode>();
    private static final HashMap<String, HttpStatusCode> lookupName = new HashMap<String, HttpStatusCode>();

    static {
        for (HttpStatusCode s : EnumSet.allOf(HttpStatusCode.class)){
            lookupCode.put(s.getValue(), s);
            lookupName.put(s.name().toUpperCase(), s);
        }
    }

    private int _val;

    HttpStatusCode(int _val) {
        this._val = _val;
    }

    public int getValue() {
        return _val;
    }

    public static HttpStatusCode get(int code) {
        return lookupCode.get(code);
    }

    public static HttpStatusCode get(String enumName) {
        return lookupName.get(enumName.toUpperCase());
    }
}