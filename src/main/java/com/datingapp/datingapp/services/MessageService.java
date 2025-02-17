package com.datingapp.datingapp.services;

import com.datingapp.datingapp.repository.MessRepo;
import com.datingapp.datingapp.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
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
