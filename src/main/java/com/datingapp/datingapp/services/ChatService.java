package com.datingapp.datingapp.services;

import com.datingapp.datingapp.controller.MessageController;
import com.datingapp.datingapp.entity.*;
import com.datingapp.datingapp.exception.ChatAlreadyExistsException;
import com.datingapp.datingapp.exception.ChatNotFoundException;
import com.datingapp.datingapp.exception.UserNotExistsExceptions;
import com.datingapp.datingapp.repository.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.RequiredArgsConstructor;
import org.hibernate.grammars.hql.HqlParser;
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
            GroupChat gr = groupChatRepo.findGroupChatByPkGroupChat(chatId);
            if(gr == null) {
                throw new ChatNotFoundException("Не существует такого чата: " + chatId);
            }
            List<ChatMemberDTO> usersChats = getChatMembersFromObject(userRepo.findGroupChatPartners(userId,chatId));
            return usersChats;
        }
        catch (ChatNotFoundException ex){
            throw new ChatNotFoundException(ex.getMessage());
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
    public List<GroupChatDto> getChats1(int userId){
        try {
            List<ChatMember> members = chatMemberRepo.findByUserId(userId);
            List<GroupChat> chatIds = members.stream()
                    .map(ChatMember::getChatId)
                    .toList();
            List<GroupChatDto> groupChatDtos = new ArrayList<>();
            for(GroupChat groupChat : chatIds){//groupChats
                Integer pkGroupChat = groupChat.getPkGroupChat();
                String name = "";
                //byte[] image = null;
                if(groupChat.getIsGroup()) {
                    name = groupChat.getName();
                    //image = groupChat.getImage();
                }
                else{
                    //List<Object[]> nameImages = groupChatRepo.getUserInfo(pkGroupChat, userId);
                    //Object[] nameImage = nameImages.get(0);
                    name = groupChatRepo.getUserName(pkGroupChat, userId).getFirst();
                    // Если результат NULL (например, пользователь не найден)
//                    if (nameImage == null || nameImage.length < 2) {
//                        name = "";
//                        //image = null;
//                    } else {
//                        name = (nameImage[0] != null) ? nameImage[0].toString() : "";
////                        try {
////                            image = (nameImage[1] != null) ? (byte[]) nameImage[1] : null;
////                        } catch (ClassCastException e) {
////                            image = null;
////                        }
//                    }
                }
                String message = messRepo.getLastMessage(pkGroupChat);
                GroupChatDto groupChatDto = new GroupChatDto(pkGroupChat, name,null, message);
                groupChatDtos.add(groupChatDto);
            }
            //log.info("Полученные чаты: " + groupChatDtos.toString());
            return groupChatDtos;
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при поиске чата: " + e.getMessage());
        }
    }

    @Transactional
    public List<GroupChatDto> getChats(int userId, int limit, int offset){
        try {
            List<GroupChatDto> groupChatDtos =
                    getChatsInfoFromObject(groupChatRepo.getChatsInfo(userId, limit, offset));
            return groupChatDtos;
        }
        catch (Exception e) {
            throw new ChatNotFoundException("Ошибка при поиске чата: " + e.getMessage());
        }
    }

    @Transactional
    public List<GroupChatDto> getChatAvatars(int userId, List<Integer> chatIds){
        try {
            List<GroupChatDto> groupChatDtos = new ArrayList<>();
            List<Object[]> avatars = groupChatRepo.findAvatars(chatIds, userId);
            for (Object[] avatar : avatars) {
                GroupChatDto groupChatDto = new GroupChatDto();
                groupChatDto.setPkGroupChat((Integer) avatar[0]);
                groupChatDto.setImage((byte[]) avatar[1]);
                groupChatDtos.add(groupChatDto);
            }
            return groupChatDtos;
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при получении аватарок чатов: " + e.getMessage());
        }
    }


    private List<GroupChatDto> getChatsInfoFromObject(List<Object[]> chatsInfo) {
        List<GroupChatDto> groupChatDtos = new ArrayList<>();
        for (var chatInfo : chatsInfo) {
            GroupChatDto groupChatDto = new GroupChatDto();
            groupChatDto.setPkGroupChat((((Number) chatInfo[0]).intValue()));
            groupChatDto.setName((String) chatInfo[1]);
            groupChatDto.setLastMessage(((String) chatInfo[2]));
            groupChatDtos.add(groupChatDto);
        }
        return groupChatDtos;
    }



    @Transactional
    public GroupChatInfoDto getChatInfo(int chatId){
        try {
            List<ChatMemberDTO> chatMember = getChatMembersFromObject(chatMemberRepo.findByChatId(chatId));
            List<Object[]> groupChat = groupChatRepo.getGroupChatInfoById(chatId);
            Integer createdBy = (Integer) groupChat.getFirst()[0];
            Timestamp createdAt = (Timestamp) groupChat.getFirst()[1];
            Boolean isGroup = (Boolean) groupChat.getFirst()[2];
            GroupChatInfoDto groupChatInfoDto = new GroupChatInfoDto(createdBy, createdAt, chatMember, isGroup);
            log.info("Полученная доп. информация о чате с id " + chatId + ": " + groupChatInfoDto.toString());
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
            //chatMemberDTO.setAvatar((byte[]) chatMember[2]);
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
            log.info("Создан чат: " + groupChat.toString());
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
            log.info("Добавлен пользователь с id "+ user_id + " в чат с id " +chat_id + ": " + chatMember.toString());
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при добавлении юзера: " + e.getMessage());
        }
    }


    @Transactional
    public void deleteChatMember(int chat_id, int user_id, int creator_id) throws UserNotExistsExceptions {
        GroupChat groupChat = groupChatRepo.getGroupChatByPkGroupChat(chat_id);
        if(groupChat == null){
            throw new ChatNotFoundException("Нет чата с таким id + " +chat_id);
        }
        if(!groupChat.getCreatedBy().equals(creator_id)){
            throw new RuntimeException("Удалять участников чата может только создатель чата!");
        }
        try{
            int count = chatMemberRepo.deleteMember(user_id, chat_id);
            if (count < 1) {
                throw new UserNotExistsExceptions("Нет такого пользователя или чата");
            }
            log.info("Удалён пользователь с id " + user_id + " из чата с id " + chat_id);
        }
        catch (UserNotExistsExceptions ex){
            throw new UserNotExistsExceptions("Ошибка при удалении пользователя чата: " + ex.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении пользователя из чата: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteChat(int chat_id, int creator_id) {
        GroupChat groupChat = groupChatRepo.getGroupChatByPkGroupChat(chat_id);
        if(groupChat == null){
            throw new ChatNotFoundException("Нет чата с таким id + " +chat_id);
        }
        if(!groupChat.getCreatedBy().equals(creator_id)){
            throw new RuntimeException("Удалять чат может только создатель чата!");
        }
        try{
            groupChatRepo.deleteById(chat_id);
            log.info("Удалён чат с id " + chat_id);
        }
        catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении чата: " + e.getMessage());
        }

    }

}
