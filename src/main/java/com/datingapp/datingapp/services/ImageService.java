package com.datingapp.datingapp.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ImageService {

    public int handleFileUpload(@RequestBody MyPic myPic){
        //log.info("Сама фотка: " + myPic);
        Picture pic = new Picture(myPic.getImageId(), new Timestamp(System.currentTimeMillis()),
                myPic.getImage());
        pic.setPkPicture(picRepo.findMaxPk()+1);
        Picture temp = picRepo.save(pic);
        log.info("Фотография загружена: " + temp.toString());
        userPicRepo.save(new UserPic(temp.getPkPicture(), myPic.getUserId()));
        return temp.getPkPicture();
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

}