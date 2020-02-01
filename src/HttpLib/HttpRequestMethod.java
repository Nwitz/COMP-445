package HttpLib;

public enum HttpRequestMethod {

    // Supported for assignment
    POST("POST"), GET("GET");

    private String _val;

    HttpRequestMethod(String _val) {
        this._val = _val;
    }

    @Override
    public String toString() {
        return _val;
    }
}
