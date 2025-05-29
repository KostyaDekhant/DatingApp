package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.MyPic;
import com.datingapp.datingapp.exception.ImageNotFoundException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.PicRepo;
import com.datingapp.datingapp.repository.UserPicRepo;
import com.datingapp.datingapp.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.hibernate.id.IncrementGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController {

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);
    private final ImageService imageService;

    //Загрузка фотографий на сервер
    @PostMapping("/users/{userId}/images/upload")
    public ResponseEntity<Integer> imageUpload(@PathVariable("userId") int userId, @RequestBody MyPic myPic){
        Integer id = imageService.imageUpload(myPic);
        return ResponseEntity.ok(id);
    }

    //Загрузка фотографий на сервер с postman'а
    @PostMapping("/users/{userId}/images/upload/v2")
    public ResponseEntity<Integer> imageUpload2(@RequestParam("image") MultipartFile image,
                                                @PathVariable("userId") int user_id,
                                                @RequestParam("image_id")int image_id) throws IOException {
        Integer id = imageService.imageUpload2(image, user_id, image_id);
        return ResponseEntity.ok(id);
    }

    //удалить фотографию
    @DeleteMapping("/images/{imageId}/delete")
    public ResponseEntity<Void> deleteImage(@PathVariable("imageId") int imageId) throws ImageNotFoundException {
        imageService.deleteImage(imageId);
        return ResponseEntity.ok().build();
    }


    //Получить фотки конкретного пользователя
    @GetMapping("/users/{userId}/images")
    public ResponseEntity<List<Object[]>> getImages(@PathVariable("userId") int user_id,
                                                    @RequestParam("limit") int limit){
        List<Object[]> obj = imageService.getImages(user_id, limit);
        log.info("Получены фотографии для пользователя c id " + user_id + ": " + obj);
        return ResponseEntity.ok(obj);
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
