package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.Like;
import com.datingapp.datingapp.exception.LikeAlreadyExistsException;
import com.datingapp.datingapp.exception.LikeNotFoundException;
import com.datingapp.datingapp.repository.LikeRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepo likeRepo;
    private static final Logger log = LoggerFactory.getLogger(LikeService.class);

    @Transactional
    public int setLike(Like like) {
        if (likeRepo.isLikeExists(like.getLiker(), like.getPoster())) {
            throw new LikeAlreadyExistsException("Лайк уже был поставлен");
        }
        like.setTime(new Timestamp(System.currentTimeMillis()));
        like.setPkLike(likeRepo.findMaxPk() + 1);
        log.info("Поставлен лайк: {}", like.toString());
        return likeRepo.save(like).getPkLike();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getMyLikes(int userId) {
        try {
            List<Object[]> objects = likeRepo.findByLiker(userId);
            return likeRepo.findByLiker(userId);
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getReceivedLikes(int userId) {
        return likeRepo.findByReceiver(userId);
    }

    @Transactional
    public int deleteLike(int liker, int poster) {
        int deleted = likeRepo.deleteLike(liker, poster);
        if (deleted == 0) {
            throw new LikeNotFoundException("Лайк не найден");
        }
        log.info("Удалён лайк между {} → {}", liker, poster);
        return deleted;
    }
}
