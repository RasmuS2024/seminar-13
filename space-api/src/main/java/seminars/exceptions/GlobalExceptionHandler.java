package seminars.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import seminars.dto.ValidationErrorResponse;
import seminars.dto.ValidationErrorResponse.FieldError;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> FieldError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .rejectedValue(error.getRejectedValue())
                        .build())
                .toList();
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Некорректные параметры запроса")
                .path(request.getRequestURI())
                .fieldErrors(fieldErrors)
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(SpaceOperationException.class)
    public ResponseEntity<ValidationErrorResponse> handleSpaceOperationException(
            SpaceOperationException ex,
            HttpServletRequest request) {
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .fieldErrors(List.of())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ValidationErrorResponse> handleDuplicateResourceException(
            DuplicateResourceException ex,
            HttpServletRequest request) {
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .fieldErrors(List.of())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .fieldErrors(List.of())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ValidationErrorResponse> handleNoResourceFound(
            NoResourceFoundException ex,
            HttpServletRequest request) {
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Запрашиваемый ресурс не найден")
                .path(request.getRequestURI())
                .fieldErrors(List.of())
                .build();
        log.warn("Ресурс не найден: {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({
            HttpMediaTypeNotAcceptableException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpRequestMethodNotSupportedException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ValidationErrorResponse> handleSpringWebExceptions(
            Exception ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex instanceof HttpRequestMethodNotSupportedException) {
            status = HttpStatus.METHOD_NOT_ALLOWED;
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
        } else if (ex instanceof HttpMediaTypeNotAcceptableException) {
            status = HttpStatus.NOT_ACCEPTABLE;
        }

        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Некорректный запрос: " + ex.getMessage())
                .path(request.getRequestURI())
                .fieldErrors(List.of())
                .build();

        log.warn("Spring Web Exception на {} : {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ValidationErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Произошла непредвиденная ошибка")
                .path(request.getRequestURI())
                .fieldErrors(List.of())
                .build();
        log.error("Непредвиденная ошибка на {} : ", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
