package antifraud.exceptions;

import org.springframework.http.HttpStatus;

public class ExistingIpException extends ResponseException{

    public ExistingIpException(HttpStatus status) {
        super(status);
    }
}