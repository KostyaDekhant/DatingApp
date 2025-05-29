package com.datingapp.datingapp.controller;

import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.services.ChatService;
import com.datingapp.datingapp.services.MessageService;
import com.datingapp.datingapp.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class GroupChatController {
    private static final Logger log = LoggerFactory.getLogger(GroupChatController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;

    @Autowired
    public GroupChatController(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
    }

//    @MessageMapping("/group_chats/create")
//    public int addChat(@RequestBody GroupChatDto groupChatDto) throws UserNotExistsExceptions {
//        log.info("Добавление чата");
//        GroupChat groupChat = chatService.saveGroupChat(groupChatDto);
//        int chatId = groupChat.getPkGroupChat();
//        groupChatDto.setPkGroupChat(chatId);
//        for(ChatMemberDTO chatMember : groupChatDto.getGroupChatInfoDto().getMembers()){
//            simpMessagingTemplate.convertAndSend(
//                    "/topic/group_chats/"+chatMember.getUserId()+"/created",
//                    groupChatDto
//            );
//        }
//        return chatId;
//    }

//    @MessageMapping("/group_chats/update")
//    public void updateGroupChat(@RequestBody GroupChatPayloadInfo info) {
//        try {
//            int chatId = info.getChatId();
//            int userId = info.getUserId();
//            String name = info.getName();
//            byte[] image = null;
//            if(info.getImage() != null)
//                image = info.getImage();
//            List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();
//            chatService.updateGroupChat(chatId, userId, name, image);
//
//            GroupChatDto groupChatDto = chatService.getChat(chatId,userId);
//
//            simpMessagingTemplate.convertAndSend(
//                    "/topic/group_chats/"+chatId+"/updated",
//                    groupChatDto
//            );
//        }
//        catch (Exception e) {
//            throw new ChatNotFoundException("Ошибка при обновлении чата: " + e.getMessage());
//        }
//    }

    @MessageMapping("/group_chats/{chatId}/remove")
    public void removeChat(@PathVariable("chatId") int chatId, @RequestBody GroupChatPayloadInfo info){
        try {
            int creatorId = info.getUserId();
            //List<ChatMemberDTO> chatMemberDTOS = chatService.getChatInfo(chatId).getMembers();

            GroupChatDto groupChatDto = chatService.getChat(chatId, creatorId);
            chatService.deleteChat(chatId, creatorId);

            simpMessagingTemplate.convertAndSend(
                    "/topic/group_chats/"+chatId+"/deleted",
                    groupChatDto
            );
            log.info("Отправлена информация о удалении чата");
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при удалении чата: " + e.getMessage());
        }
    }
}
