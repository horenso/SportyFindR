package at.ac.tuwien.sepm.groupphase.backend.exception;

public class NotFoundInDatabaseException extends Exception {
    public NotFoundInDatabaseException() {
    }

    public NotFoundInDatabaseException(String message) {
        super(message);
    }

    public NotFoundInDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundInDatabaseException(Exception e) {
        super(e);
    }
}
