package sit.int221.sastt3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {
    private final Map<String, String> errorMessage;
    public InvalidRequestException(String message, Map<String, String> errors) {
        super(message);
        this.errorMessage = errors;
    }
    public Map<String, String> getErrors() {
        return errorMessage;
    }
}
