package HttpLib;

public enum HttpFlag {
    HEADER("-h"),
    INLINE_DATA("-d"),
    FILE("-f");

    private String _val;

    HttpFlag(String s) {
        this._val = s;
    }

    public String getValue() {
        return _val;
    }
}
