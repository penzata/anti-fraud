package antifraud.exceptions;

public class ExistingAdministratorException extends BusinessException {

    public ExistingAdministratorException(String message) {
        super(message);
    }

    public ExistingAdministratorException() {
    }
}