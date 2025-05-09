package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.Chat;
import com.datingapp.datingapp.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRepo extends JpaRepository<GroupChat, Integer> {

    GroupChat getGroupChatByPkGroupChat(int chatId);
}
