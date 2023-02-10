package antifraud.exceptions;

public class AlreadyProvidedException extends BusinessException {

    public AlreadyProvidedException(String message) {
        super(message);
    }

    public AlreadyProvidedException() {
    }
}