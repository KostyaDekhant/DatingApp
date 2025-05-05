package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.Like;
import com.datingapp.datingapp.entity.LikeDTO;
import com.datingapp.datingapp.exception.LikeAlreadyExistsException;
import com.datingapp.datingapp.exception.LikeNotFoundException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.LikeRepo;
import com.datingapp.datingapp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepo likeRepo;
    private final UserRepo userRepo;
    private static final Logger log = LoggerFactory.getLogger(LikeService.class);

    @Transactional
    public int setLike(LikeDTO likeDTO) {
        if (likeRepo.isLikeExists(likeDTO.getLiker(), likeDTO.getPoster())) {
            throw new LikeAlreadyExistsException("Лайк уже был поставлен");
        }
        Like like = new Like();
        like.setLiker(likeDTO.getLiker());
        like.setPoster(likeDTO.getPoster());
        like.setTime(new Timestamp(System.currentTimeMillis()));
        like.setPkLike(likeRepo.findMaxPk() + 1);
        log.info("Поставлен лайк: {}", like.toString());
        return likeRepo.save(like).getPkLike();
    }

    @Transactional(readOnly = true)
    public List<LikeDTO> getMyLikes(int userId) {
        try {
            return  getLikesFromObject(likeRepo.findByLiker(userId), userId);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<LikeDTO> getReceivedLikes(int userId) {
        try {
            List<LikeDTO> obj = getLikesFromObject(likeRepo.findByReceiver(userId), userId);
            return obj;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional
    public int deleteLike(LikeDTO likeDTO) throws UserNotExistsExceptions {
        int liker = likeDTO.getLiker();
        int poster = likeDTO.getPoster();

        if(userRepo.findById(liker).isEmpty()) {
            throw new UserNotExistsExceptions("Нет юзера с таким id: "+liker);
        }
        if(userRepo.findById(poster).isEmpty()) {
            throw new UserNotExistsExceptions("Нет юзера с таким id: "+poster);
        }

        int deleted = likeRepo.deleteLike(liker, poster);
        if (deleted == 0) {
            throw new LikeNotFoundException("Лайк не найден");
        }
        log.info("Удалён лайк между {} и {}", liker, poster);
        return deleted;
    }

    private List<LikeDTO> getLikesFromObject(List<Object[]> objects, int userId) {
        return objects.stream().map(cols -> {
            Integer   liker    = (Integer)   cols[0];
            Timestamp time     = (Timestamp) cols[1];
            String    name     = (String)    cols[2];
            byte[]    image    = (byte[])    cols[3];
            java.sql.Date sqlDate = (java.sql.Date) cols[4];
            LocalDate   birthday = sqlDate.toLocalDate();
            return new LikeDTO(liker, userId, time, name, image, birthday);
        }).toList();
    }
}
