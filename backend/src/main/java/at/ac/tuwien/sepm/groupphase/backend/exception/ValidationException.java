package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ValidationException extends Exception {

    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Exception e) {
        super(e);
    }
}
