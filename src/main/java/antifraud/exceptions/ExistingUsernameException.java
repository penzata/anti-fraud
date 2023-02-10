package antifraud.exceptions;

import org.springframework.http.HttpStatus;

public class ExistingUsernameException extends ResponseException {

    public ExistingUsernameException(HttpStatus status) {
        super(status);
    }

}