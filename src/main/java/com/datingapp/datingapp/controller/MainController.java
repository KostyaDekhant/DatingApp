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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final UserRepo userRepo;
    private final ObjectMapper objectMapper;

    //Добавление пользователя
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

    //Удаление пользователей по id
    @DeleteMapping("/api")
    public void deleteUser(@RequestParam int id)
    {
        userRepo.deleteById(id);//
    }

    @GetMapping("/api/specific")
    public ResponseEntity<User> getSpecificUser(@RequestParam String name)
    {
        User user = userRepo.findByName(name);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }
}

