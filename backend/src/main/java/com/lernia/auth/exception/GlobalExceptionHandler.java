package com.lernia.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid parameter value");
        errorResponse.put("parameter", ex.getName());
        errorResponse.put("value", ex.getValue());
        
        String message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumConstants = ex.getRequiredType().getEnumConstants();
            if (enumConstants != null && enumConstants.length > 0) {
                message += String.format(". Valid values are: %s", 
                    java.util.Arrays.toString(enumConstants));
            }
        }
        
        errorResponse.put("message", message);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
