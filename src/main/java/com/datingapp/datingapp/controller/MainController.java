package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.repository.*;

import com.datingapp.datingapp.services.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;


import java.sql.Timestamp;
import java.util.List;


//@Slf4j
@RestController
@RequiredArgsConstructor
public class MainController {

    private final UserRepo userRepo;
    private final ChatRepo chatRepo;
    private final ResidRepo residRepo;
    private final PicRepo picRepo;
    private final LikeRepo likeRepo;
    private final UserPicRepo userPicRepo;
    private final ObjectMapper objectMapper;

    private static final Logger log = LoggerFactory.getLogger(MainController.class);


    @Autowired
    private PasswordService passwordService;

    //Ненужная штука
    @GetMapping("/api/specific")
    public ResponseEntity<User> getSpecificUser(@RequestParam String name)
    {
        User user = userRepo.findByName(name);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    //Установить место жительства || В целом не реализована почти что, нужно переписать
    @PostMapping("/api/residence")
    public int setResidence(@RequestBody Residence resid)
    {
        int user_id = resid.getPkUser();
        if(!userRepo.findById(user_id).isEmpty())
        {
            Residence temp = residRepo.findByPk_user(resid.getPkUser());
            if(temp != null)
            {
                int id = temp.getPkResidence();
                resid.setPkResidence(id);
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

    //Получить место жительства || дописать
    @GetMapping("/api/residence/{pk_user}")
    public Residence getResidence(@RequestParam int pk_user) {
        return residRepo.findByPk_user(pk_user);
    }

}

