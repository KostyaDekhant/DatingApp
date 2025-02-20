package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.MyPic;
import com.datingapp.datingapp.entity.Picture;
import com.datingapp.datingapp.entity.User_pic;
import com.datingapp.datingapp.repository.PicRepo;
import com.datingapp.datingapp.repository.UserPicRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final PicRepo picRepo;
    private final UserPicRepo userPicRepo;

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    //Загрузка фотографий на сервер || ответку переделать тоже*
    @PostMapping("/api/user_images/upload")
    public int handleFileUpload(@RequestBody MyPic myPic)
    {
        //log.info("Сама фотка: " + myPic);
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

    //удалить фотографию ||*
    @DeleteMapping("/api/user_images/delete/{image_id}")
    public int deleteImage(@PathVariable int image_id)
    {
        int whos_pic = picRepo.findUserById(image_id);
        int res = picRepo.deleteImage(image_id);
        picRepo.updateId(whos_pic);
        log.info("Удалена фотография с id: " + image_id);
        return res;
    }


    //Получить фотки конкретного пользователя || хз, подумать надо будет*
    @GetMapping("/api/user_images/{user_id}")
    public List<Object[]> getImages(@PathVariable int user_id)
    {
        List<Object[]> obj = picRepo.findByUserId(user_id);
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
}
