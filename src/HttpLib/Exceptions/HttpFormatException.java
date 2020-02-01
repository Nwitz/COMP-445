package HttpLib.Exceptions;

public class HttpFormatException extends Exception {
    public HttpFormatException() {
        super("Header entry not well formatted.");
    }

    public HttpFormatException(String message) {
        super(message);
    }
}
