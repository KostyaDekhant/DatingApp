package com.datingapp.datingapp.exception;

import com.datingapp.datingapp.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ClobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @ExceptionHandler(UserAlreadyExistsExceptions.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsExceptions ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Некорректные данные: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(UserExceptionsWithCode.class)
    public ResponseEntity<Integer> handleUserException(UserExceptionsWithCode ex) {
        log.error("Ошибка авторизации: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getErrorCode());
    }

    @ExceptionHandler(UserNotExistsExceptions.class)
    public ResponseEntity<Integer> handleUserNotExistsException(UserNotExistsExceptions ex) {
        log.error("Нет пользователя с таким id: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getErrorCode());
    }

    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<Void> handleImageNotFoundException(ImageNotFoundException ex) {
        log.error("Нет фотографии с таким id: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<Void> handleChatAlreadyExistsException(ChatAlreadyExistsException ex) {
        log.error("Ошибка при создании чата: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(FormsNotFoundException.class)
    public ResponseEntity<Void> handleFormsNotFoundException(FormsNotFoundException ex) {
        log.error("Анкеты не найдены : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<Void> handleLikeAlreadyExistsException(LikeAlreadyExistsException ex) {
        log.error("Лайк уже поставлен: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<Void> handleLikeNotFoundException(LikeNotFoundException ex) {
        log.error("Лайк не найден: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


}
