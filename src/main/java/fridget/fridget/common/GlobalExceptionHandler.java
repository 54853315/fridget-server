package fridget.fridget.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(
            new CommonResponse(HttpStatus.BAD_REQUEST, e.getMessage(), null), 
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(
            new CommonResponse(HttpStatus.NOT_FOUND, e.getMessage(), null), 
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> handleGenericException(Exception e) {
        return new ResponseEntity<>(
            new CommonResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null), 
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}