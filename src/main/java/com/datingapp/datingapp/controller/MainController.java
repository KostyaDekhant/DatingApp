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

    //Искать юзеров, с кем есть общий чат || id юзер теперь
    @GetMapping("/api/chat_users/{pk_user}")
    public List<Object[]> findChatUsers(@PathVariable int pk_user)
    {
        List<Object[]> obj = userRepo.findUsers(pk_user);
        log.info("Общие чаты: " + obj);
        return obj;
    }

    //Поставить лайк || результат лишь поменять, а так збс
    @PostMapping("api/likes")
    public int setLike(@RequestBody Like like)
    {
        if(likeRepo.isLikeExists(like.getLiker(), like.getPoster()))
        {
            log.info("Лайк уже был поставлен!");
            return -2;
        }
        Timestamp time = new Timestamp(System.currentTimeMillis());
        like.setTime(time); //, image_id
        log.info("Поставлен лайк: "+like.toString());
        like.setPkLike(likeRepo.findMaxPk()+1);

        //проверка на взаимный лайк (создание чата) create_chat

        return likeRepo.save(like).getPkLike();
    }

    //Лайки, которые поставил клиент || тоже хз, мейби так
    @GetMapping("api/my_likes/{user_id}")
    public List<Object[]> getLikesList(@RequestParam("user_id") int user_id)
    {
        List<Object[]> obj = likeRepo.findByLiker(user_id);
        log.info("Мои лайки: "+ obj);
        return obj;
    }

    //Лайки, которые поставили клиенту || по идее не так *
    @GetMapping("api/received_likes/{user_id}")
    public List<Object[]> getReceivedLikesList(@PathVariable int user_id)
    {
        List<Object[]> obj = likeRepo.findByReceiver(user_id);
        log.info("Лайки на мои фотографии: "+ obj);
        return obj;
    }

    //Анкеты || вроде норм
    @GetMapping("api/forms")
    public Object[] getListUsers(@RequestParam("user_id") int user_id,
                                 @RequestParam("prev_user_id") int prev_user_id)
    {
        Object[] obj = userRepo.findQuestUsers(user_id, prev_user_id);
        log.info("Анкеты : "+ obj);
        return obj;
    }

    //Убрать лайк || переписать *
    @DeleteMapping("/api/likes")
    int deleteLike(@RequestParam("liker") int liker,
                   @RequestParam("poster") int poster)
    {
        int delete_count = likeRepo.deleteLike(liker,poster);
        if(delete_count != 0)
            log.info("Лайк убран: " + delete_count);
        return delete_count;
    }

    //Создание чата по запросу || норм, ответку переписать только*
    @PostMapping("api/chats")
    int createChat(@RequestParam("pk_user") int pk_user,
                   @RequestParam("pk_user1") int pk_user1)
    {
        if(!chatRepo.isChatExists(pk_user, pk_user1))
        {
            Chat chat = new Chat(pk_user, pk_user1);
            chat.setPkChat(chatRepo.findMaxPk()+1);
            log.info("ID созданного чата: "+ chat);
            chatRepo.save(chat);
            log.info("Создан новый чат: " + chat);
            return chat.getPkChat();
        }
        log.info("Такой чат уже существует!");
        return -1;
    }

}

