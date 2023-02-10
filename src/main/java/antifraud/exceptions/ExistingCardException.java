package antifraud.exceptions;

import org.springframework.http.HttpStatus;

public class ExistingCardException extends ResponseException {

    public ExistingCardException(HttpStatus status) {
        super(status);
    }
}
