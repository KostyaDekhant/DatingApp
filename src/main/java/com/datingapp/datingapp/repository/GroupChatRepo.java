package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.Chat;
import com.datingapp.datingapp.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatRepo extends JpaRepository<GroupChat, Integer> {

    GroupChat getGroupChatByPkGroupChat(int chatId);


    @Query(value = """
SELECT
COALESCE(u.name, '') AS name,          
p.image  AS image 
FROM chat_member cm
LEFT JOIN "user" u ON cm.user_id = u.pk_user
LEFT JOIN user_pic up ON u.pk_user = up.pk_user
LEFT JOIN picture p ON up.pk_picture = p.pk_picture AND p.id = 1
WHERE cm.user_id != :userId AND cm.chat_id = :chatId LIMIT 1
""", nativeQuery = true)
    List<Object[]> getUserInfo(int chatId, int userId);

}