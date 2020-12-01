package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ServiceException extends Exception{

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Exception e) {
        super(e);
    }
}
