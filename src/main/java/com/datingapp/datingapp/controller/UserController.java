package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.entity.UserDTO;
import com.datingapp.datingapp.exception.UserExceptionsWithCode;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.services.PasswordService;
import com.datingapp.datingapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    private final UserRepo userRepo;


    //@Autowired
    private final PasswordService passwordService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    //Добавление пользователя
    @PostMapping("/users")
    public ResponseEntity<Integer> addUser(@Validated @RequestBody User user) {
        Integer id = userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    //Получение данных о пользователе
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable int id) {
        Optional<User> temp = userRepo.findById(id);
        if (temp.isPresent()){
            UserDTO userDTO = new UserDTO(temp.get());
            log.info("Информация о пользователе: {}", userDTO);
            return ResponseEntity.ok(userDTO);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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
    public ResponseEntity<Void> deleteUser(@PathVariable int id)
    {
        if(!userRepo.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        log.info("Удалён пользователь с id: " + id);
        userRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
