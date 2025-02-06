package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.enitity.*;
import com.datingapp.datingapp.repository.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;

@Slf4j
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

    //Добавление пользователя
    @PostMapping("/api/users")
    public void AddUser(@RequestBody User user)
    {
        log.info("Новый пользователь: " + userRepo.save(user));
    }

    //Вывод пользователей
    @SneakyThrows
    @GetMapping("/api/users")
    public List<User> getAll()
    {
        List<User> temp = userRepo.findAll();
        log.info("Список пользователей: " + temp);
        return temp;
    }

    //Получение данных о пользователе
    @GetMapping("/api/users/{id}")
    public User getUser(@PathVariable int id)
    {
        User temp = userRepo.findById(id).get();
        log.info("Информация о пользователе: " + temp);
        return temp;
    }

    //Обновление данных пользователя
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

    //Регистрация || переписать с использованием ResponseEntity<String>
    @PostMapping("/api/signup")
    public int signupUser(@RequestBody User user) {
        User temp = userRepo.findByLogin(user.getLogin());
        if(temp == null)
        {
            AddUser(user);
            return userRepo.findByLogin(user.getLogin()).getPk_user();
        }
        log.info("Пользователь с таким логином уже существует!");
        return -1;
    }

    //Авторизация || тоже самое, как и для регистрации
    @PostMapping("/api/login")
    public int loginUser(@RequestBody User user) {
        User temp = userRepo.findByLogin(user.getLogin());
        if(temp != null)
        {
            if(temp.getPassword().equals(user.getPassword()))
                log.info("Пользователь успешно вошёл в систему!");
            else
                log.info("Пользователь ввёл пароль неверно!");
            return temp.getPassword().equals(user.getPassword()) ? temp.getPk_user() : -1;
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

    //Получить место жительства || дописать
    @GetMapping("/api/residence/{pk_user}")
    public Residence getResidence(@RequestParam int pk_user) {
        return residRepo.findByPk_user(pk_user);
    }

    //Искать юзеров, с кем есть общий чат || id юзер теперь
    @GetMapping("/api/chat_users/{pk_user}")
    public List<Object[]> findChat_users(@RequestParam int pk_user)
    {
        List<Object[]> obj = userRepo.findUsers(pk_user);
        log.info("Общие чаты: " + obj);
        return obj;
    }

    //Загрузка фотографий на сервер || ответку переделать тоже
    @PostMapping("api/images")
    public int handleFileUpload(@RequestBody MyPic myPic)
    {
        log.info("Сама фотка: " + myPic);
        Picture pic = new Picture(myPic.getImage_id(), new Timestamp(System.currentTimeMillis()),
                myPic.getImage());
        pic.setPk_picture(picRepo.findMaxPk()+1);
        Picture temp = picRepo.save(pic);
        log.info("Фотография загружена: " + temp.toString());
        userPicRepo.save(new User_pic(temp.getPk_picture(), myPic.getUser_id()));
        return temp.getPk_picture();
        /*try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            //Сделать проверки на фотки

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }*/
    }

    //Загрузка фотографий на сервер с postman'а || пусть будет, потом переделать лучше
    @PostMapping("api/images2")
    public int handleFileUpload2(@RequestParam("image") MultipartFile image, @RequestParam("user_id") int user_id,
                                 @RequestParam("image_id")int image_id) throws IOException {
        Picture pic = new Picture(image_id, new Timestamp(System.currentTimeMillis()),
                image.getBytes());
        pic.setPk_picture(picRepo.findMaxPk()+1);
        Picture temp = picRepo.save(pic);
        log.info("информация о фото " + temp.toString());
        userPicRepo.save(new User_pic(temp.getPk_picture(), user_id));
        return temp.getPk_picture();
        /*try {
            // Проверяем, существует ли директория, если нет - создаем
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }


        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }*/
    }

    //удалить фотографию ||
    @DeleteMapping("api/images/{image_id}")
    public int deleteImage(@RequestParam("image_id")int image_id)
    {
        int whos_pic = picRepo.findUserById(image_id);
        int res = picRepo.deleteImage(image_id);
        picRepo.updateId(whos_pic);
        log.info("Удалена фотография с id: " + image_id);
        return res;
    }


    //Получить фотки конкретного пользователя || хз, подумать надо будет
    @GetMapping("api/images/{pk_user}")
    public List<Object[]> getImages(@RequestParam("pk_user") int id)
    {
        List<Object[]> obj = picRepo.findByUserId(id);
        log.info("Получены фотографии для пользователя: " + obj);
        return obj;
    }

    //Определение формата фото
    public boolean identifyImageFormat(byte[] imageBytes) {

        if ((imageBytes[0] & 0xFF) == 0xFF && (imageBytes[1] & 0xFF) == 0xD8 &&
                (imageBytes[imageBytes.length - 2] & 0xFF) == 0xFF && (imageBytes[imageBytes.length - 1] & 0xFF) == 0xD9) {
            return true;
        }
        return false;
    }


    public byte[] convertByteTobyte(Byte[] byteArray) {
        byte[] bytes = new byte[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            bytes[i] = byteArray[i]; // Автоматическая распаковка Byte в byte
        }
        return bytes;
    }


    //Конверт byte[] в Byte[]
    public static Byte[] convertbytetoByte(byte[] bytes) {
        Byte[] byteObjects = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteObjects[i] = bytes[i];  // Автоупаковка примитивного типа byte в объект Byte
        }
        return byteObjects;
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
        like.setPk_like(likeRepo.findMaxPk()+1);
        return likeRepo.save(like).getPk_like();
    }

    //Лайки, которые поставил клиент || тоже хз, мейби так
    @GetMapping("api/my_likes/{user_id}")
    public List<Object[]> getLikesList(@RequestParam("user_id") int user_id)
    {
        List<Object[]> obj = likeRepo.findByLiker(user_id);
        log.info("Мои лайки: "+ obj);
        return obj;
    }

    //Лайки, которые поставили клиенту || по идее не так
    @GetMapping("api/received_likes/{user_id}")
    public List<Object[]> getreceivedLikesList(@RequestParam("user_id") int user_id)
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

    //Убрать лайк || переписать
    @DeleteMapping("api/likes/{liker}")
    int deleteLike(@RequestParam("liker") int liker,
                   @RequestParam("poster") int poster)
    {
        int delete_count = likeRepo.deleteLike(liker,poster);
        if(delete_count != 0)
            log.info("Лайк убран: " + delete_count);
        return delete_count;
    }

    //Создание чата по запросу || норм, ответку переписать только
    @PostMapping("api/chats")
    int createChat(@RequestParam("pk_user") int pk_user,
                   @RequestParam("pk_user1") int pk_user1)
    {
        if(!chatRepo.isChatExists(pk_user, pk_user1))
        {
            Chat chat = new Chat(pk_user, pk_user1);
            chat.setPk_chat(chatRepo.findMaxPk()+1);
            log.info("ID созданного чата: "+ chat);
            chatRepo.save(chat);
            log.info("Создан новый чат: " + chat);
            return chat.getPk_chat();
        }
        log.info("Такой чат уже существует!");
        return -1;
    }

}

