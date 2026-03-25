package org.hotel.controller;

import org.hotel.model.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Map<String, String>> buildResponse(HttpStatus status, String message, Exception e) {
        Map<String, String> response = new HashMap<>();
        String errorMessage;
        if (message != null) {
            errorMessage = message;
        } else {
            errorMessage = e.getClass().getSimpleName();
        }
        response.put("error", errorMessage);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler({RoomNotFoundException.class, GuestNotFoundException.class, ServiceNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFountExceptions(RuntimeException e) {
        return buildResponse(HttpStatus.NOT_FOUND, e.getMessage(), e);
    }

    @ExceptionHandler({RoomAlreadyOccupiedException.class,
            RoomAlreadyInServiceException.class,
            RoomAlreadyAvailableException.class,
            RoomAlreadyExistsException.class,
            ServiceAlreadyExistsException.class,
            RoomNotOccupiedException.class})
    public ResponseEntity<Map<String, String>> handleConflictExceptions(RuntimeException e) {
        return buildResponse(HttpStatus.CONFLICT, e.getMessage(), e);
    }

    @ExceptionHandler({WrongSortTypeException.class, ChangeStatusBannedException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequestExceptions(RuntimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage(), e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllOtherExceptions(Exception e) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Внутрення ошибка", e);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException e) {
        return buildResponse(HttpStatus.NOT_FOUND, "Указанный URL-адрес не существует: " + e.getResourcePath(), e);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException e) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "У вас нет прав на выполнение данной операции");

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return buildResponse(HttpStatus.METHOD_NOT_ALLOWED, "Метод " + e.getMethod() + " не поддерживается для этого адреса.", e);
    }

    @ExceptionHandler({WrongPasswordException.class, UserNotFoundException.class})
    public ResponseEntity<Map<String, String>> handle(RuntimeException e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handle(UserAlreadyExistsException e) {
        return buildResponse(HttpStatus.CONFLICT, "Ошибка при регистрации: пользователь уже существует", e);
    }
}
