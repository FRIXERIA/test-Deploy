package sit.int221.sastt3.exception;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> HandlerInvalidData(InvalidRequestException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), request.getDescription(false).substring(4));
        Map<String, String> errors = exception.getErrors();
        for (String field : errors.keySet()) {
            errorResponse.addValidationError(field, errors.get(field));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
//
//
//}
//@RestControllerAdvice
//public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
//    @ExceptionHandler(InvalidRequestException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex, WebRequest request) {
//        List<ErrorDetail> errorDetails = new ArrayList<>();
//
//        if (ex.getCause() instanceof CategoryNotFoundException) {
//            errorDetails.add(new ErrorDetail("categoryId", "does not exist"));
//        }
//
//        if (ex.getFieldError("announcementDescription") != null) {
//            errorDetails.add(new ErrorDetail("announcementDescription", "must not be blank"));
//        }
//
//        // add more error details for other fields
//
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setTitle("Announcement attributes validation failed!");
//        errorResponse.setInstance(request.getDescription(false));
//        errorResponse.setDetail(errorDetails);
//
//        return ResponseEntity.badRequest().body(errorResponse);
//    }
//    @ExceptionHandler(InvalidRequestException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex, WebRequest request) {
//        ErrorResponse errorResponse = new ErrorResponse();
//        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
//        errorResponse.setTitle("Announcement attributes validation failed!");
//        errorResponse.setInstance(request.getDescription(false));
//        errorResponse.addErrorDetail("categoryId", "does not exist");
//        errorResponse.addErrorDetail("announcementDescription", "must not be blank");
//        // add more error details for other fields
//
//        return ResponseEntity.badRequest().body(errorResponse);
//    }
//    public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex) {
//    //public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException ex, WebRequest request) {
//        //ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false).substring(4));
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage() );
//        // Add validation errors to error response
//        errorResponse.addValidationError("announcementTitle", "Title size must between 1 and 200");
//        errorResponse.addValidationError("announcementDescription", "Description size must between 1 and 10000");
//        errorResponse.addValidationError("announcementDescription", "must not be blank");
//        errorResponse.addValidationError("closeDate", "must be a future date");
//        errorResponse.addValidationError("categoryId", "doesn't exists");
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//
//    }
//}
