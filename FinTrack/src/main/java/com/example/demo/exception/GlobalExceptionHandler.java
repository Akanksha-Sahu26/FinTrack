package com.example.demo.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(value=ResourceNotFoundException.class)
   public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex){
	   ErrorResponse error=new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage());
	   return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
   }
	@ExceptionHandler(value = ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(
            ResourceAlreadyExistsException ex) {

        ErrorResponse error =
                new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequestException(
	        BadRequestException ex) {

	    ErrorResponse error = new ErrorResponse(
	            HttpStatus.BAD_REQUEST.value(),
	            ex.getMessage()
	    );

	    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
	        MethodArgumentNotValidException ex) {

	    Map<String, String> errorsMap = new HashMap<>();

	    BindingResult bResult = ex.getBindingResult();
	    List<FieldError> fieldErrors = bResult.getFieldErrors();

	    for (FieldError error : fieldErrors) {
	        errorsMap.put(error.getField(), error.getDefaultMessage());
	    }

	    return new ResponseEntity<>(errorsMap, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception ex) {

	    ErrorResponse error =
	            new ErrorResponse(
	                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                    ex.getMessage()
	            );

	    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
