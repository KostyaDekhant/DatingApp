package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.Dislike;
import com.datingapp.datingapp.repository.DislikeRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class DislikeService {
    private DislikeRepo dislikeRepo;

    private static final Logger log = LoggerFactory.getLogger(DislikeService.class);
    @Transactional
    public void setDislike(int disliker, int poster) {
        try{
            log.info("Ставим дизлайк");
            Dislike dislike = new Dislike(disliker,
                                          poster,
                                          new Timestamp(System.currentTimeMillis()));
            dislikeRepo.save(dislike);
            log.info("Дизлайк успешно поставлен юзером с id " + disliker + " юзеру с id "+  poster );
        }
        catch(Exception e){
            throw new RuntimeException("Ошибка при дизлайке: " + e.getMessage());
        }
    }
}
