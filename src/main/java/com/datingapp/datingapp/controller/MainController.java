package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.enitity.Picture;
import com.datingapp.datingapp.enitity.User_pic;
import com.datingapp.datingapp.repository.PicRepo;
import com.datingapp.datingapp.repository.UserPicRepo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.datingapp.datingapp.enitity.User;
import com.datingapp.datingapp.enitity.Residence;
import com.datingapp.datingapp.repository.ResidRepo;
import com.datingapp.datingapp.repository.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.time.Instant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final Path uploadPath = Paths.get("C:/datingapp/pictures");

    private final UserRepo userRepo;
    private final ResidRepo residRepo;
    private final PicRepo picRepo;
    private final UserPicRepo userPicRepo;
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
    public boolean updateUser(@RequestBody User user)
    {
        if(userRepo.findById(user.getPk_user()).isEmpty()) //
        {
            return false;
        }
        User temp = userRepo.findById(user.getPk_user()).get();

        userRepo.save(updateData(temp, user));
        return true;
    }

    private User updateData(User oldU, User newU)
    {
        if(!newU.getName().equals(oldU.getName()) && !newU.getName().equals(""))
            oldU.setName(newU.getName());
        if(newU.getAge() != oldU.getAge() && newU.getAge() != new Date())
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

    @PostMapping("/api/residence")
    public int setResidence(@RequestBody Residence resid)
    {
        int user_id = resid.getPk_user();
        if(!userRepo.findById(user_id).isEmpty())
        {
            Residence temp = residRepo.findByPk_user(resid.getPk_user());
            if(temp != null)
            {
                int id = temp.getPk_residence();
                resid.setPk_residence(id);
                log.info("update resid info: " + residRepo.save(resid));
                return 2;
            }
            else{
                log.info("new resid info: " + residRepo.save(resid));
                return 1;
            }
        }
        log.info("Пользователь с данным id отсутствует!");
        return -1;
    }

    @GetMapping("/api/residence")
    public Residence getResidence(@RequestParam int pk_user) {
       return residRepo.findByPk_user(pk_user);
    }


    @GetMapping("/api/chat_users")
    public List<Object[]> findChat_users(@RequestParam int pk_user)
    {
        return userRepo.findUsers(pk_user);
    }

    @PostMapping("api/upload_image")
    public int handleFileUpload(@RequestParam("image") byte[] image, @RequestParam("user_id") int user_id,
                                @RequestParam("image_id")int image_id) {
        try {
            // Проверяем, существует ли директория, если нет - создаем
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            //Files.copy(file.getInputStream(), this.uploadPath.resolve(file.getOriginalFilename()));
            Picture pic = new Picture(image_id, new Timestamp(System.currentTimeMillis()),
                    image);
            pic.setPk_picture(picRepo.findMaxPk()+1);
            log.info("информация о фото " + pic.toString());

            userPicRepo.save(new User_pic(picRepo.save(pic).getPk_picture(), user_id));
            return pic.getPk_picture();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @GetMapping("api/user_images")
    public List<Object[]> getImages(@RequestParam("pk_user") int id) {
        return picRepo.findByUserId(id);
    }

    ////convertBytetobyte(pic.getBytea())   //Byte[]

    public static Byte[] convertBytetobyte(byte[] bytes) {
        Byte[] byteObjects = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteObjects[i] = bytes[i];  // Автоупаковка примитивного типа byte в объект Byte
        }
        return byteObjects;
    }

}

