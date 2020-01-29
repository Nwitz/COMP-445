package HttpLib;

public enum HttpStatusCode {
    OK(200), Created(201), Accepted(202), NoContent(204),
    BadRequest(400), Unauthorized(401), Forbidden(403), NotFound(404),
    InternalServerError(500);

    private int _val;

    HttpStatusCode(int _val) {
        this._val = _val;
    }

    public int getValue() {
        return _val;
    }
}