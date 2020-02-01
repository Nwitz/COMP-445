package HttpLib.Exceptions;

public class InvalidRequestException extends Exception {
    public InvalidRequestException() {
        super("Invalid request. Cannot process or use invalid requests.");
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}
