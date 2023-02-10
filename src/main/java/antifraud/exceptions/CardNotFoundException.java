package antifraud.exceptions;

public class CardNotFoundException extends BusinessException {

    public CardNotFoundException(String message) {
        super(message);
    }
}
