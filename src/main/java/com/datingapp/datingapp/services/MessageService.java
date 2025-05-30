package com.datingapp.datingapp.services;

import com.datingapp.datingapp.entity.MessageDTO;
import com.datingapp.datingapp.entity.MessageRead;
import com.datingapp.datingapp.repository.MessRepo;
import com.datingapp.datingapp.entity.Message;
import com.datingapp.datingapp.repository.MessageReadRepo;
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
    private final MessageReadRepo messageReadRepo;

    public MessageService(MessRepo messRepo, MessageReadRepo messageReadRepo) {
        this.messRepo = messRepo;
        this.messageReadRepo = messageReadRepo;
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

    public Message getMessageByPkMessage(Integer messageId) {
        return messRepo.getMessageByPkMessage(messageId);
    }

    public void saveReadMessage(MessageRead messageRead) {
        messageReadRepo.save(messageRead);
    }

    public boolean existsByMessage_IdAndUser_Id(int messageId, int userId) {
        return messRepo.existsByPkMessageAndPkUser(messageId, userId);
    }
}
