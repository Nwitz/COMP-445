package HttpLib.Exceptions;

public class InvalidResponseException extends Exception {
    public InvalidResponseException() {
        super("Invalid response.");
    }

    public InvalidResponseException(String message) {
        super(message);
    }
}
