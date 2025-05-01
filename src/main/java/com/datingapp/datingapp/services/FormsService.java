package com.datingapp.datingapp.services;

import com.datingapp.datingapp.exception.FormsNotFoundException;
import com.datingapp.datingapp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FormsService {
    private final UserRepo userRepo;

    @Transactional(readOnly = true)
    public Object[] findQuestUsers(int userId, int prevUserId) {
        Object[] result = userRepo.findQuestUsers(userId, prevUserId);
        if (result == null || result.length == 0) {
            throw new FormsNotFoundException("Анкеты не найдены для userId=" + userId);
        }
        return result;
    }
}
