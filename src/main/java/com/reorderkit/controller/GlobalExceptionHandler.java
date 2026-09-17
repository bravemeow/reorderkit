package com.reorderkit.controller;

import com.reorderkit.dto.ApiErrorResponse;
import com.reorderkit.exception.StoreNotFoundException;
import com.reorderkit.exception.ProductVariantNotFoundException;
import com.reorderkit.exception.ReorderNotReadyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ProductVariantNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleVariantNotFound(ProductVariantNotFoundException exception) {
        return new ApiErrorResponse(404, "Not Found", exception.getMessage(), Instant.now());
    }

    @ExceptionHandler(ReorderNotReadyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleReorderNotReady(ReorderNotReadyException exception) {
        return new ApiErrorResponse(409, "Conflict", exception.getMessage(), Instant.now());
    }

    @ExceptionHandler(StoreNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleStoreNotFound(StoreNotFoundException exception) {
        return new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                exception.getMessage(),
                Instant.now()
        );
    }
}
