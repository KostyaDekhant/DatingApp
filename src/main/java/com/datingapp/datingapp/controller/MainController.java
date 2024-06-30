package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.enitity.User;
import com.datingapp.datingapp.enitity.Cat;
import com.datingapp.datingapp.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static java.time.Instant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;

    //Добавление пользователя ()
    @PostMapping("/api/add")
    public void AddUser(@RequestBody User user)
    {
        log.info("new row: " + userRepo.save(user));
    }

    //Вывод пользователей
    @SneakyThrows
    @GetMapping("/api/all")
    public List<User> getAll()
    {
        return userRepo.findAll();
    }

    //Получение данных о пользователе
    @GetMapping("/api/user")
    public User getUser(@RequestParam int id)
    {
        return userRepo.findById(id).get();
    }

    //Обновление данных пользователя
    @PostMapping("/api/user")
    public String updateUser(@RequestBody User user)
    {
        if(userRepo.findById(user.getPk_user()).isEmpty() == true)
        {
            return "Попытка обновить данные несуществующего пользователя!";
        }
        userRepo.save(user);
        return "Данные обновлены успешно!";
    }

    //Регистрация
    @PostMapping("/api/signup")
    public int signupUser(@RequestBody User user) { //String login, @RequestParam String pass
        // Обработка данных запроса
        User temp = userRepo.findByLogin(user.getLogin());
        if(temp == null)
        {
            AddUser(user);
            return userRepo.findByLogin(user.getLogin()).getPk_user();
        }
        return -1;
    }

    //Авторизация
    @PostMapping("/api/login")
    public int loginUser(@RequestBody User user) { //String login, @RequestParam String pass
        // Обработка данных запроса
        User temp = userRepo.findByLogin(user.getLogin());
        if(temp != null)
        {
            return temp.getPassword().equals(user.getPassword()) ? temp.getPk_user() : -1;
        }
        return -2;
    }

    //Удаление пользователей по id
    @DeleteMapping("/api")
    public void deleteUser(@RequestParam int id)
    {
        userRepo.deleteById(id);
    }

    @GetMapping("/api/specific")
    public ResponseEntity<User> getSpecificUser(@RequestParam String name)
    {
        User user = userRepo.findByName(name);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}

