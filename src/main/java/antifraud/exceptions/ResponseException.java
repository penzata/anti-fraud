package antifraud.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ResponseException extends ResponseStatusException {

    public ResponseException(HttpStatus status) {
        super(status);
    }

    public ResponseException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public ResponseException(HttpStatus status, String reason, Throwable cause) {
        super(status, reason, cause);
    }

    public ResponseException(int rawStatusCode, String reason, Throwable cause) {
        super(rawStatusCode, reason, cause);
    }
}
