package com.techelevator.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

@ControllerAdvice
public class ConstraintViolationHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(ConstraintViolationException exception, ServletWebRequest webRequest) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.UNPROCESSABLE_ENTITY.value(), exception.getMessage());
    }
}