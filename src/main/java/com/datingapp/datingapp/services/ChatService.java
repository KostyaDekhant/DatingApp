package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.MessageController;
import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ChatAlreadyExistsException;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.repository.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepo chatRepo;
    private final MessRepo messRepo;
    private final UserRepo userRepo;
    private final GroupChatRepo groupChatRepo;
    private final ChatMemberRepo chatMemberRepo;

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Transactional(readOnly = true)
    public List<ChatDTO> findChatUsers(int userId) {
        try {
            List<ChatDTO> usersChats = getChatsFromObject(userRepo.findChatPartners(userId));
            return usersChats;
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске чатов: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ChatMemberDTO> findGroupChatUsers(int userId, int chatId) {
        try {
            List<ChatMemberDTO> usersChats = getChatMembersFromObject(userRepo.findGroupChatPartners(userId,chatId));
            return usersChats;
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при поиске чатов: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ChatDTO getChatById(int userId, int chatId){
        try {
            ChatDTO chat = getChatFromObject(userRepo.findChat(userId, chatId));
            return chat;
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при поиске чата: " + e.getMessage());
        }
    }

    private List<ChatDTO> getChatsFromObject(List<Object[]> chatPartners) {
        return chatPartners.stream().map(cols -> {
            ChatDTO dto = new ChatDTO();
            dto.setPartnerName((String) cols[0]);
            dto.setChatId(((Number) cols[1]).intValue());
            dto.setLastMessage((String) cols[2]);
            dto.setPartnerId(((Number) cols[3]).intValue());
            dto.setAvatar((byte[]) cols[4]);
            return dto;
        }).toList();
    }

    private ChatDTO getChatFromObject(List<Object[]> chatPartners) {
        Object[] cols = chatPartners.getFirst();
        ChatDTO dto = new ChatDTO();
        dto.setPartnerName((String) cols[0]);
        dto.setChatId(((Number) cols[1]).intValue());
        dto.setLastMessage((String) cols[2]);
        dto.setPartnerId(((Number) cols[3]).intValue());
        dto.setAvatar((byte[]) cols[4]);
        return dto;
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

    @Transactional
    public List<GroupChatDto> getChats(int userId){
        try {
            List<ChatMember> members = chatMemberRepo.findByUserId(userId);
            List<GroupChat> chatIds = members.stream()
                    .map(ChatMember::getChatId)
                    .toList();
            //List<GroupChat> groupChats = groupChatRepo.findAllById(chatIds);
            List<GroupChatDto> groupChatDtos = new ArrayList<>();
            for(GroupChat groupChat : chatIds){//groupChats
                Integer pkGroupChat = groupChat.getPkGroupChat();
                String name = "";
                byte[] image = null;
                if(groupChat.getIsGroup()) {
                    name = groupChat.getName();
                    image = groupChat.getImage();
                }
                else{
                    log.info(pkGroupChat + " " + userId);
                    Object[] nameImage = groupChatRepo.getUserInfo(pkGroupChat, userId);
                    name = nameImage[0].toString();
                    image = (byte[]) nameImage[1];
                }
                String message = messRepo.getLastMessage(pkGroupChat);
                GroupChatDto groupChatDto = new GroupChatDto(pkGroupChat, name, image, message);
                groupChatDtos.add(groupChatDto);
            }
            return groupChatDtos;
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при поиске чата: " + e.getMessage());
        }
    }

    @Transactional
    public GroupChatInfoDto getChatInfo(int chatId){
        try {
            List<ChatMemberDTO> chatMember = getChatMembersFromObject(chatMemberRepo.findByChatId(chatId));
            GroupChat groupChat = groupChatRepo.findById(chatId).orElse(null);
            Integer createdBy = groupChat.getCreatedBy();
            Timestamp createdAt = groupChat.getCreatedAt();
            Boolean isGroup = groupChat.getIsGroup();
            GroupChatInfoDto groupChatInfoDto = new GroupChatInfoDto(createdBy, createdAt, chatMember, isGroup);
            return groupChatInfoDto;
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при поиске чата: " + e.getMessage());
        }
    }

    private List<ChatMemberDTO> getChatMembersFromObject(List<Object[]> chatMembers) {
        List<ChatMemberDTO> chatMemberDTOs = new ArrayList<>();
        for (var chatMember : chatMembers) {
            ChatMemberDTO chatMemberDTO = new ChatMemberDTO();
            chatMemberDTO.setUsername((String) chatMember[0]);
            chatMemberDTO.setUserId(((Number) chatMember[1]).intValue());
            chatMemberDTO.setAvatar((byte[]) chatMember[2]);
            chatMemberDTOs.add(chatMemberDTO);
        }
        return chatMemberDTOs;
    }

    @Transactional
    public Integer saveGroupChat(GroupChatDto groupChatDto) {
        try {
            GroupChat groupChat = new GroupChat();
            GroupChatInfoDto groupChatInfoDto = groupChatDto.getGroupChatInfoDto();

            groupChat.setName(groupChatDto.getName());
            groupChat.setCreatedAt(groupChatInfoDto.getCreatedAt());
            groupChat.setCreatedBy(groupChatInfoDto.getCreatedBy());
            groupChat.setIsGroup(groupChatInfoDto.getIsGroup());
            groupChat.setImage(groupChatDto.getImage());
            groupChatRepo.save(groupChat);

            for(ChatMemberDTO chatMemberDTO : groupChatInfoDto.getMembers()){
                ChatMember chatMember = new ChatMember();
                chatMember.setChatId(groupChat);
                chatMember.setUserId(chatMemberDTO.getUserId());
                chatMember.setJoinedAt(new Timestamp(System.currentTimeMillis()));
                //chatMember.setRole("");
                chatMemberRepo.save(chatMember);
            }

            return groupChat.getPkGroupChat();
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении информации о групповых чатах: " + e.getMessage());
        }
    }

    @Transactional
    public void addMember(int chat_id, int user_id) {
        try {
            GroupChat groupChat = groupChatRepo.findById(chat_id).orElse(null);
            ChatMember chatMember = new ChatMember();
            chatMember.setChatId(groupChat);
            chatMember.setUserId(user_id);
            chatMember.setJoinedAt(new Timestamp(System.currentTimeMillis()));
            chatMemberRepo.save(chatMember);
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении юзера: " + e.getMessage());
        }
    }

}
