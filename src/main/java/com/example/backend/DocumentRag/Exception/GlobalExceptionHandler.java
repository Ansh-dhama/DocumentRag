package com.example.backend.DocumentRag.Exception;
import com.example.backend.DocumentRag.Dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.retry.TransientAiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(value = ResourseNotFoundException.class)
    public ResponseEntity<?> handleException(ResourseNotFoundException e) {
       logger.warn("Resourse not found : {}" ,e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder()
                        .data(null)
                        .message(e.getMessage())
                        .success(false)
                .build());
   }
    @ExceptionHandler(value = DocumentException.class)
    public ResponseEntity<?> handleProcessingError(DocumentException e) {
        logger.warn("Document processing error : {}" ,e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ApiResponse.builder()
                .data(null)
                .message("Failed size limit exceed:  " + e.getMessage())
                .success(false)
                .build());
    }
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeError(MaxUploadSizeExceededException e) {
        logger.warn("Document size is exceed : {}" ,e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ApiResponse.builder()
                .data(null)
                .message("File size is greater than limit  " + e.getMessage())
                .success(false)
                .build());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        for(FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        logger.warn("Method  not valid : {}" ,errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.<Map<String,String>>builder()
                .data(null)
                .message("Valid failed  " + e.getMessage())
                .success(false)
                        .data(errors)
                .build());
    }
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentError(IllegalArgumentException e) {
        logger.warn("Illegal argument : {}" ,e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(ApiResponse.builder()
                .data(null)
                .message(e.getMessage())
                .success(false)
                .build());
    }
   @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> generalException(Exception e) {
        logger.warn("Exception : {}" ,e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ApiResponse.builder()

        .data(null)
        .message(e.getMessage())
        .success(false)
                .build());
   }

    // Temporary AI provider failure: 503
    @ExceptionHandler(TransientAiException.class)
    public ResponseEntity<ApiResponse<Void>> handleTransientAiException(
            TransientAiException e) {

        logger.error("Temporary AI model failure: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.<Void>builder()
                        .data(null)
                        .message("AI service is temporarily unavailable. Please try again later.")
                        .success(false)
                        .build());
    }

    // Non-recoverable AI provider failure: 502
    @ExceptionHandler(NonTransientAiException.class)
    public ResponseEntity<ApiResponse<Void>> handleNonTransientAiException(
            NonTransientAiException e) {

        logger.error("Non-transient AI model failure: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.<Void>builder()
                        .data(null)
                        .message("AI service failed to process your request.")
                        .success(false)
                        .build());
    }

}
