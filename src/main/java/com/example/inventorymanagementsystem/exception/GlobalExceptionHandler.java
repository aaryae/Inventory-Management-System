package com.example.inventorymanagementsystem.exception;


import com.example.inventorymanagementsystem.dtos.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler   {

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ApiResponse> handleDataNotFoundException(DataNotFoundException ex) {
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundExceptionHandler.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundExceptionHandler(ResourceNotFoundExceptionHandler ex) {
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmployeeNotFoundExceptionHandler.class)
    public ResponseEntity<ApiResponse> handleEmployeeNotFoundExceptionHandler(EmployeeNotFoundExceptionHandler ex) {
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<ApiResponse> handleDuplicateEmployeeException(DuplicateEmployeeException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ValidationException.class)
        public ResponseEntity<ApiResponse> handleValidationException(ValidationException ex){
            String message =ex.getMessage();
            ApiResponse apiResponse=new ApiResponse(message, false);
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }

    @ExceptionHandler(BatchLimitException.class)
    public ResponseEntity<ApiResponse> handleBatchLimitException(BatchLimitException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiResponse> handleBusinessLogicException(BusinessLogicException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, ex.success, ex.data);
        return new ResponseEntity<>(apiResponse, ex.status);
    }

    @ExceptionHandler(InvalidBatchException.class)
    public ResponseEntity<ApiResponse> handleInvalidBatchException(InvalidBatchException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BarcodeGenerationException.class)
    public ResponseEntity<ApiResponse> handleBarcodeGenerationException(BarcodeGenerationException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ExcelParsingException.class)
    public ResponseEntity<ApiResponse> handleExcelParsingException(ExcelParsingException ex){
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(
                new ApiResponse("Validation failed", false, errors)
        );
    }

}

