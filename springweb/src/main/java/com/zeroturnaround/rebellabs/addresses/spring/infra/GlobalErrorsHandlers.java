package com.zeroturnaround.rebellabs.addresses.spring.infra;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.zeroturnaround.rebellabs.addresses.api.exceptions.NotFoundException;


@ControllerAdvice
public class GlobalErrorsHandlers {

    @ExceptionHandler({ NotFoundException.class })
    public ResponseEntity<String> notFoundHandler(NotFoundException notFound) {
        return new ResponseEntity<>(notFound.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<String> genericErrorHandler(Exception error) {
        error.printStackTrace();
        return new ResponseEntity<>(error.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
