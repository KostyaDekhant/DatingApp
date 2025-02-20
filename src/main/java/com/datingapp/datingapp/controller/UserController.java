package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.User;
import com.datingapp.datingapp.repository.UserRepo;
import com.datingapp.datingapp.services.PasswordService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserRepo userRepo;

    @Autowired
    private PasswordService passwordService;
    //можно попробовать сделать общий
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    //Добавление пользователя
    @PostMapping("/api/users")
    public void AddUser(@RequestBody User user) {
        log.info("Новый пользователь: {}", userRepo.save(user));
    }

    //Получение данных о пользователе*
    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable int id)
    {
        User temp = userRepo.findById(id).get();
        log.info("Информация о пользователе: {}", temp);
        return temp;
    }

    //Обновление данных пользователя*
    @PatchMapping("/api/users")
    public boolean updateUser(@RequestBody User user)
    {
        if(userRepo.findById(user.getPk_user()).isEmpty()) //
        {
            log.info("Данные не обновлены, так как нет пользователя с таким id!");
            return false;
        }
        User temp = userRepo.findById(user.getPk_user()).get();
        userRepo.save(updateData(temp, user));
        log.info("Данные обновлены!");
        return true;
    }

    //Учёт обновления данных || тут другое
    private User updateData(User oldU, User newU)
    {
        if(!newU.getName().equals(oldU.getName()) && !newU.getName().equals(""))
            oldU.setName(newU.getName());
        if(!newU.getAge().equals(oldU.getAge()) && newU.getAge() != null)
            oldU.setAge(newU.getAge());
        if(!newU.getGender().equals(oldU.getGender()) && !newU.getGender().equals(""))
            oldU.setGender(newU.getGender());
        if(newU.getHeight() != oldU.getHeight() && newU.getHeight() != 0)
            oldU.setHeight(newU.getHeight());
        if(newU.getIs_online() != oldU.getIs_online())
            oldU.setIs_online(newU.getIs_online());
        if(newU.getLast_online() != oldU.getLast_online() && !newU.getLast_online().equals(""))
            oldU.setLast_online(newU.getLast_online());
        if(!newU.getPassword().equals(oldU.getPassword()) && !newU.getPassword().equals(""))
            oldU.setPassword(newU.getPassword());
        if(!newU.getDescription().equals(oldU.getDescription()) && !newU.getDescription().equals(""))
            oldU.setDescription(newU.getDescription());
        if(!newU.getLogin().equals(oldU.getLogin()) && !newU.getLogin().equals(""))
            oldU.setLogin(newU.getLogin());
        return oldU;
    }

    //Регистрация || переписать с использованием ResponseEntity<String>*
    @PostMapping("/api/signup")
    public int signupUser(@RequestBody User user) {

        User temp = userRepo.findByLogin(user.getLogin());
        if(temp == null)
        {
            String password = user.getPassword();
            String hashedPassword = passwordService.hashPassword(password);
            user.setPassword(hashedPassword);
            String salt = passwordService.extractSalt(hashedPassword); //saltGenerator();
            user.setSalt(salt);
            log.info("Соль: {}", salt);
            AddUser(user);
            return userRepo.findByLogin(user.getLogin()).getPk_user();
        }
        log.info("Пользователь с таким логином уже существует!");
        return -1;
    }

    //Авторизация || тоже самое, как и для регистрации*
    @PostMapping("/api/login")
    public int loginUser(@RequestBody User user) {
        User temp = userRepo.findByLogin(user.getLogin());
        if(temp != null)
        {
            if(passwordService.verifyPassword(user.getPassword(),temp.getPassword())) {
                log.info("Пользователь успешно вошёл в систему!");
                return temp.getPk_user();
            }
            else {
                log.info("Пользователь ввёл пароль неверно!");
                return -1;
            }
            //return temp.getPassword().equals(user.getPassword()) ? temp.getPk_user() : -1;
        }
        log.info("Пользователя с таким логином не существует!");
        return -2;
    }

    //Удаление пользователей по id || почти ок
    @DeleteMapping("/api/users/{id}")
    public void deleteUser(@RequestParam int id)
    {
        log.info("Удалён пользователь с id: " + id);
        userRepo.deleteById(id);
    }

}
