package com.internship.flow_appointment_scheduling.infrastructure.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleUserNotFoundException(UserNotFoundException e) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());

    problemDetail.setTitle(e.getTitle());

    return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleValidationExceptions(MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage, Collectors.toList())
        ))
        .entrySet()
        .stream()
        .map(entry -> entry.getKey() + ": " + String.join(", ", entry.getValue()))
        .collect(Collectors.toList());

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Validation Error");
    problemDetail.setDetail("Validation failed for one or more fields.");
    problemDetail.setProperty("errors", errors);

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    String message = ex.getMessage();
    String invalidValue = message.substring(message.indexOf("\"") + 1, message.lastIndexOf("\""));
    String acceptedValues = message.substring(message.indexOf("[") + 1, message.indexOf("]"));

    String detail = String.format("Invalid value '%s' for enum type. Accepted values are: %s", invalidValue, acceptedValues);
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problemDetail.setTitle("Invalid Enum Value");

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
}

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    String detail = String.format("Failed to convert value of type '%s' to required type '%s'; For input string: '%s'",
        ex.getValue().getClass().getSimpleName(), ex.getRequiredType().getSimpleName(), ex.getValue());

    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
    problemDetail.setTitle("Method Argument Type Mismatch");

    return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
  }

}