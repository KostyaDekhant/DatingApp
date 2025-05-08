package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.UserController;
import com.datingapp.datingapp.entity.FormDTO;
import com.datingapp.datingapp.entity.LikeDTO;
import com.datingapp.datingapp.exception.FormsNotFoundException;
import com.datingapp.datingapp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FormsService {
    private final UserRepo userRepo;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Transactional(readOnly = true)
    public FormDTO findQuestUsers(int userId, int prevUserId) {
        FormDTO result = getFormsFromObject(userRepo.findQuestUsers(userId, prevUserId));
        if (result == null) {
            throw new FormsNotFoundException("Анкеты не найдены для userId=" + userId);
        }
        return result;
    }

    private FormDTO getFormsFromObject(List<Object[]> objects) {
        Object[] object = objects.getFirst();
        Integer   pk_user  = ((Number)    object[0]).intValue();
        String    name     = (String)    object[1];
        java.sql.Date sqlDate = (java.sql.Date) object[2];
        LocalDate birthday = sqlDate.toLocalDate();
        String    gender     = (String)    object[3];
        Integer    height     = ((Number)    object[4]).intValue();
        String    description     = (String)    object[5];
        return new FormDTO(name, birthday, gender, height, description, pk_user);
    }
}
