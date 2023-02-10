package antifraud.exceptions;

import org.springframework.http.HttpStatus;

public class ExistingFeedbackException extends ResponseException {

    public ExistingFeedbackException(HttpStatus status) {
        super(status);
    }
}