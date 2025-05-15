package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.ChatMember;
import com.datingapp.datingapp.entity.ChatMemberDTO;
import com.datingapp.datingapp.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepo extends JpaRepository<ChatMember, Integer> {
    @Query(value = """
SELECT u.name AS username, u.pk_user AS userId, p.image AS avatar
FROM "user" u
LEFT JOIN chat_member cm ON cm.user_id = u.pk_user
LEFT JOIN user_pic up ON up.pk_user = cm.user_id
LEFT JOIN picture p ON p.pk_picture = ( \s
                SELECT MIN(pk_picture)\s
                FROM "user_pic"\s
                WHERE up.pk_user = u.pk_user
            )
WHERE cm.chat_id = :chatId;
""" , nativeQuery = true)
    List<Object[]> findByChatId(Integer chatId);

    List<ChatMember> findByUserId(Integer userId);

    @Modifying
    @Query(value = """
DELETE FROM chat_member cm WHERE cm.user_id = :userId AND cm.chat_id = :chatId
""", nativeQuery = true)
    int deleteMember(@Param("userId") Integer userId, @Param("chatId") Integer chatId);
}
