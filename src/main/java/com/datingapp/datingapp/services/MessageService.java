package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.MessageDTO;
import com.datingapp.datingapp.repository.MessRepo;
import com.datingapp.datingapp.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final MessRepo messRepo;

    public MessageService(MessRepo messRepo) {
        this.messRepo = messRepo;
    }

    public Message saveMessage(Message message) {
        return messRepo.save(message);
    }

    public List<MessageDTO> getChatHistory(int chatId, int limit, int offset) {
        List<Message> messages = messRepo.findChatMessages(chatId, limit, offset);
        List<MessageDTO> messageDTO = new ArrayList<>();
        for (Message message : messages) {
            messageDTO.add(new MessageDTO(message));
        }
        return messageDTO.reversed();
    }
}
