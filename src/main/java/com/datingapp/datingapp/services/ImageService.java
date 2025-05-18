package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.ImageController;
import com.datingapp.datingapp.entity.UserPic;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.exception.ImageNotFoundException;
import com.datingapp.datingapp.repository.PicRepo;
import com.datingapp.datingapp.repository.UserPicRepo;
import com.datingapp.datingapp.entity.Picture;
import com.datingapp.datingapp.entity.MyPic;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ImageService {

    private final PicRepo picRepo;
    private final UserPicRepo userPicRepo;

    private static final Logger log = LoggerFactory.getLogger(ImageController.class);

    @Transactional
    public int imageUpload(MyPic myPic){
        //log.info("Сама фотка: " + myPic);
        try {
            Picture pic = new Picture(myPic.getImageId(),
                    new Timestamp(System.currentTimeMillis()),
                    myPic.getImage());
            pic.setPkPicture(picRepo.findMaxPk() + 1);
            Picture temp = picRepo.save(pic);
            log.info("Фотография загружена: " + temp.toString());
            userPicRepo.save(new UserPic(temp.getPkPicture(), myPic.getUserId()));
            return temp.getPkPicture();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке изображения!");
        }
    }

    @Transactional
    public int imageUpload2(MultipartFile image,
                            int user_id,
                            int image_id) throws RuntimeException{
        try {
            Picture pic = new Picture(image_id,
                    new Timestamp(System.currentTimeMillis()),
                    image.getBytes());
            pic.setPkPicture(picRepo.findMaxPk()+1);
            Picture temp = picRepo.save(pic);
            log.info("информация о фото " + temp.toString());
            userPicRepo.save(new UserPic(temp.getPkPicture(), user_id));
            return temp.getPkPicture();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Ошибка при загрузке изображения!");
        }
    }

    @Transactional
    public void deleteImage(int image_id) throws ImageNotFoundException {
        Optional<Integer> whosPic = picRepo.findUserById(image_id);
//        if(whosPic.isPresent()){}
//            throw new ImageNotFoundException("Нет пользователя с такой фотографией!");
        try {
            picRepo.deleteImage(image_id);
            picRepo.updateId(whosPic.get());
            log.info("Удалена фотография с id: " + image_id);
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при удалении фотографии!");
        }
    }

    @Transactional
    public List<Object[]> getImages(int userId, int limit){
        try {
            List<Object[]> obj = picRepo.findByUserId(userId, limit);
            //log.info("Получены фотографии для пользователя: " + obj);
            return obj;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}