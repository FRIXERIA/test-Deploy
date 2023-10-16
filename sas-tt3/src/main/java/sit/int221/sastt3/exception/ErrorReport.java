package sit.int221.sastt3.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ErrorReport extends RuntimeException {
    private final String errorMessage;
    public ErrorReport(String message, String errors) {
        super(message);
        this.errorMessage = errors;
    }
    public String getErrors() {
        return errorMessage;
    }
}
