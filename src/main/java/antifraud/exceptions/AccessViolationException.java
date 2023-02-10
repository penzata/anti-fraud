package antifraud.exceptions;

public class AccessViolationException extends BusinessException {

    public AccessViolationException(String message) {
        super(message);
    }

    public AccessViolationException() {
    }
}
