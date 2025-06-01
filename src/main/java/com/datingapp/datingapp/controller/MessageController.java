package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.GroupChatRepo;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.FcmService;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.security.Principal;

import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final NotificationController notificationController;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;
    private final UserService userService;
    private final FcmService fcmService;

    record ReadMessageNotification(int userId, List<Integer> messageIds) {};

    @Autowired
    public MessageController(MessageService messageService, NotificationController notificationController,
                             SimpMessagingTemplate simpMessagingTemplate, ChatService chatService,
                             UserService userService, FcmService fcmService) {
        this.notificationController = notificationController;
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
        this.fcmService = fcmService;
    }

    // Эндпоинт для отправки тестового сообщения всем клиентам
    @GetMapping("/broadcast")
    public String broadcast() {
        String message = "Это тестовое сообщение для всех!";
        notificationController.broadcastMessage(message);
        return "Сообщение отправлено";
    }


    @MessageMapping("/send")
    //@SendToUser("/topic/messages/{chat_id}")
    public void sendMessage(@RequestBody MessageDTO messageDTO) { //MessageDTO
        log.info(messageDTO.toString());
        Message mess = new Message(messageDTO);
        int userId = messageDTO.getPkUser();
        int chat_id = mess.getPkChat();
        try {
            List<ChatMemberDTO> chatMemberDTOs = chatService.getChatMembers(chat_id);

            simpMessagingTemplate.convertAndSend(
                    "/topic/messages/" + chat_id,
                    messageService.saveMessage(mess));

            for (ChatMemberDTO chatMember : chatMemberDTOs) {
                if(chatMember.getUserId() != userId) {
                    User user = userService.getUserById(chatMember.getUserId());
                    Boolean isOnline = user.getIsOnline();
                    String token = user.getFcmToken();
                    log.info("Попытка отправить смс пользователю с id " + chatMember.getUserId());
                    if (token != null) {
                        log.info("Отправляем смс");
                        String name = chatService.getChatName(chat_id, chatMember.getUserId());
                        fcmService.sendPushNotificationToUser(
                                token,
                                name,
                                mess.getMessage(),
                                Map.of("chatId", String.valueOf(chat_id))
                        );
                    }
                }
            }
            //return messageService.saveMessage(mess);
        } catch (Exception e) {
            e.printStackTrace();
            //return null;rf
        }
    }

    @MessageMapping("/history/{chatId}")
    public List<MessageDTO> getChatHistory(@DestinationVariable int chatId,
                               @Payload HistoryRequest req) {

        List<MessageDTO> history = messageService.getChatHistory(chatId, req.getLimit(), req.getOffset());
        log.info("id " + req.getUserId());
        simpMessagingTemplate.convertAndSend(
                "/topic/"+req.getUserId()+"/history/" + chatId, // Используем /queue/ для приватных сообщений
                history
        ); //ToUser
        log.info("История отправлена " +history);
        return history;
    }

    @MessageMapping("/group_chats/{chatId}/users/{userId}/messages/read")
    public void handleReadStatus(@DestinationVariable int chatId,
                                 @DestinationVariable int userId,
                                 ReadMessagePayload payload) {
        log.info("Попытка установить статус прочитанности сообщений для чата id " + chatId + " от юзера с id " + userId);
        for (int messageId : payload.getReadMessageIds()) {
            log.info("Сообщение с id " + messageId);
            if (!messageService.existsByMessage_IdAndUser_Id(messageId, userId)) {
                Message message = messageService.getMessageByPkMessage(messageId);
                User user = userService.getUserById(userId);
                messageService.saveReadMessage(new MessageRead(message, user));
                log.info("Сообщение с id "+ messageId + " прочитано " + user.getName());
            }
        }
        log.info("Отправка прочитанных смс всем в чате с id " + chatId);
        simpMessagingTemplate.convertAndSend(
                "/topic/group_chats/" + chatId + "/read",
                new ReadMessageNotification(userId, payload.getReadMessageIds())
        );
    }
}
