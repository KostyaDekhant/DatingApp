package com.datingapp.datingapp.services;

import com.datingapp.datingapp.enitity.ChatMessageDto;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.controller.MessageController;
import com.datingapp.datingapp.repository.MessRepo;
import com.datingapp.datingapp.enitity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final MessRepo messRepo;

    @Autowired
    public MessageService(MessRepo messRepo) {
        this.messRepo = messRepo;
    }

    public Message saveMessage(Message message) {
        return messRepo.save(message);
    }

    public List<Message> getChatHistory(int chatId) {
        return messRepo.findChatMessages(chatId);
    }
}
