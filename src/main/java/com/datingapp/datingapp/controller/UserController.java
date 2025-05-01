package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.entity.UserDTO;
import com.datingapp.datingapp.exception.UserExceptionsWithCode;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.PasswordService;
import com.datingapp.datingapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    //Добавление пользователя
    @PostMapping("/users")
    public ResponseEntity<Integer> addUser(@Validated @RequestBody User user) {
        Integer id = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    //Получение данных о пользователе
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) throws UserNotExistsExceptions {
        UserDTO userDTO = userService.getUser(id);
        return ResponseEntity.ok(userDTO);
    }

    //Обновление данных пользователя
    @PatchMapping("/users")
    public ResponseEntity<Void> updateUser(@RequestBody UserDTO user) throws UserNotExistsExceptions {
        Integer id = userService.updateUser(user);
        return ResponseEntity.ok().build();
    }

    //Регистрация
    @PostMapping("/signup")
    public ResponseEntity<Integer> signupUser(@RequestBody User user) {
        Integer id = userService.signupUser(user);
        return ResponseEntity.ok(id);
    }

    //Авторизация
    @PostMapping("/login")
    public ResponseEntity<Integer>  loginUser(@RequestBody User user) throws UserExceptionsWithCode {
        Integer id = userService.loginUser(user);
        return ResponseEntity.ok(id);
    }

    //Удаление пользователей по id
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) throws UserNotExistsExceptions {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
