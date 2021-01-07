package at.ac.tuwien.sepm.groupphase.backend.exception;

public class NotFoundException2 extends Exception {

    public NotFoundException2() {
    }

    public NotFoundException2(String message) {
        super(message);
    }

    public NotFoundException2(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException2(Exception e) {
        super(e);
    }
}
