package HttpLib;

import java.util.EnumSet;
import java.util.HashMap;

public enum HttpStatusCode {
    OK(200), Created(201), Accepted(202), NoContent(204),
    BadRequest(400), Unauthorized(401), Forbidden(403), NotFound(404),
    InternalServerError(500);

    private static final HashMap<Integer, HttpStatusCode> lookup = new HashMap<Integer, HttpStatusCode>();

    static {
        for (HttpStatusCode s : EnumSet.allOf(HttpStatusCode.class))
            lookup.put(s.getValue(), s);
    }

    private int _val;

    HttpStatusCode(int _val) {
        this._val = _val;
    }

    public int getValue() {
        return _val;
    }

    public static HttpStatusCode get(int code) {
        return lookup.get(code);
    }
}