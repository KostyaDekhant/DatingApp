package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.entity.UserDTO;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.services.PasswordService;
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
public class UserController {
    private final UserRepo userRepo;

    @Autowired
    private final PasswordService passwordService;
    //можно попробовать сделать общий
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    //Добавление пользователя
    @PostMapping("/api/users")
    public ResponseEntity<Integer> addUser(@Validated @RequestBody User user) {
        User savedUser = userRepo.save(user);
        log.info("Новый пользователь: {}", savedUser);
        Integer id = savedUser.getPkUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Некорректные данные: " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    //Получение данных о пользователе
    @GetMapping("/api/users/{id}")
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

    //Обновление данных пользователя*
    @PatchMapping("/api/users")
    public ResponseEntity<Void> updateUser(@RequestBody UserDTO user)
    {
        log.info("DTO: " +user);
        Integer id = user.getId();
        Optional<User> userOptional = userRepo.findById(id);
        if(userOptional.isPresent()){
            User existingUser = userOptional.get();
            User newUser = new User(user);
            userRepo.save(updateData(existingUser, newUser));
            log.info("Данные обновлены!");
            return ResponseEntity.ok().build();
        }
        else {
            log.info("Данные не обновлены, так как нет пользователя с таким id!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Учёт обновления данных
    private User updateData(User oldU, User newU)
    {
        if(!newU.getName().equals(oldU.getName()) && !newU.getName().equals(""))
            oldU.setName(newU.getName());
        if(!newU.getBirthday().equals(oldU.getBirthday()) && !newU.getBirthday().equals(""))
            oldU.setBirthday(newU.getBirthday());
        if(!newU.getGender().equals(oldU.getGender()) && !newU.getGender().equals(""))
            oldU.setGender(newU.getGender());
        if(newU.getHeight() != oldU.getHeight() && newU.getHeight() != 0)
            oldU.setHeight(newU.getHeight());
        if(newU.getIsOnline() != oldU.getIsOnline())
            oldU.setIsOnline(newU.getIsOnline());
        if(newU.getLastOnline() != oldU.getLastOnline() && !newU.getLastOnline().equals(""))
            oldU.setLastOnline(newU.getLastOnline());
        //if(!newU.getPassword().equals(oldU.getPassword()) && !newU.getPassword().equals(""))
        //    oldU.setPassword(newU.getPassword());
        if(!newU.getDescription().equals(oldU.getDescription()) && !newU.getDescription().equals(""))
            oldU.setDescription(newU.getDescription());
        //if(!newU.getLogin().equals(oldU.getLogin()) && !newU.getLogin().equals(""))
        //    oldU.setLogin(newU.getLogin());
        return oldU;
    }

    //Регистрация
    @PostMapping("/api/signup")
    public ResponseEntity<Integer> signupUser(@RequestBody User user) {
        Optional<User> tempOptional = userRepo.findByLogin(user.getLogin());
        if (!tempOptional.isPresent()) {
            String password = user.getPassword();
            String hashedPassword = passwordService.hashPassword(password);
            user.setPassword(hashedPassword);
            String salt = passwordService.extractSalt(hashedPassword); //saltGenerator();
            user.setSalt(salt);
            log.info("Соль: {}", salt);
            addUser(user);
            Integer id = userRepo.findByLogin(user.getLogin()).get().getPkUser();
            return ResponseEntity.ok().body(id);
        }
        log.info("Пользователь с таким логином уже существует!");
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    //Авторизация
    @PostMapping("/api/login")
    public ResponseEntity<Integer>  loginUser(@RequestBody User user) {
        Optional<User> tempOptional = userRepo.findByLogin(user.getLogin());
        if (tempOptional.isPresent()) {
            User temp = tempOptional.get();
            if(passwordService.verifyPassword(user.getPassword(),temp.getPassword())) {
                log.info("Пользователь успешно вошёл в систему!");
                return ResponseEntity.ok(temp.getPkUser());
            }
            else {
                log.info("Пользователь ввёл пароль неверно!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(-1);
            }
        }
        log.info("Пользователя с таким логином не существует!");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(-2);
    }

    //Удаление пользователей по id
    @DeleteMapping("/api/users/{id}")
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
