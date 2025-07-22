package com.example.inventorymanagementsystem.exception;


import com.example.inventorymanagementsystem.dtos.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse> handleDuplicateResourceException(DuplicateResourceException ex) {
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

}

