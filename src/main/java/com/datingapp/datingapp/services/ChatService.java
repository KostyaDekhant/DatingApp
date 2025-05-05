package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.Chat;
import com.datingapp.datingapp.entity.ChatDTO;
import com.datingapp.datingapp.exception.ChatAlreadyExistsException;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.repository.ChatRepo;
import com.datingapp.datingapp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepo chatRepo;
    private final UserRepo userRepo;

    @Transactional(readOnly = true)
    public List<ChatDTO> findChatUsers(int userId) {
        try {
            List<ChatDTO> chatUsers = userRepo.findChatPartners(userId);
            return chatUsers;
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске чатов: " + e.getMessage());
        }
    }

    @Transactional
    public int createChat(int userA, int userB) {
        try {
            if (chatRepo.isChatExists(userA, userB)) {
                throw new ChatAlreadyExistsException("Чат между " + userA + " и " + userB + " уже существует");
            }
            Chat chat = new Chat(userA, userB);
            chat.setPkChat(chatRepo.findMaxPk() + 1);
            chatRepo.save(chat);
            return chat.getPkChat();
        }
        catch (ChatAlreadyExistsException ex) {
            throw ex;
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при создании общего чата: " + e.getMessage());
        }
    }
}
