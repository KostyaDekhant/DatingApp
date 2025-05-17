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
gc.pk_group_chat       AS chat_id,
        -- если это групповая — показываем её название,
        -- иначе — берём имя единственного другого участника
        CASE
WHEN gc.is_group THEN gc.name
ELSE other_user.name
END                     AS chat_name,
lm.message              AS last_message,
lm.time                 AS last_time
FROM
chat_member cm
  -- нужные нам чаты
JOIN group_chat gc
ON cm.chat_id = gc.pk_group_chat
AND cm.user_id = :userId

  -- LATERAL-джоин: для каждой строки вытягиваем последнее сообщение
LEFT JOIN LATERAL (
        SELECT m.message, m.time
                FROM message m
                WHERE m.pk_chat = gc.pk_group_chat
                ORDER BY m.time DESC
                LIMIT 1
) lm ON TRUE

  -- LATERAL-джоин: для «прямых» чатов находим единственного другого участника
LEFT JOIN LATERAL (
        SELECT u.name
                FROM chat_member cm2
                JOIN "user" u
                ON u.pk_user = cm2.user_id
                WHERE cm2.chat_id  = gc.pk_group_chat
                AND cm2.user_id <> :userId
                LIMIT 1
) other_user ON TRUE

ORDER BY
lm.time DESC  -- сортируем чаты от самых «свежих»
""", nativeQuery = true)
    List<Object[]> getChatsInfo(int userId);



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

    @Query(value = """
SELECT       
p.image  AS image 
FROM chat_member cm
LEFT JOIN "user" u ON cm.user_id = u.pk_user
LEFT JOIN user_pic up ON u.pk_user = up.pk_user
LEFT JOIN picture p ON up.pk_picture = p.pk_picture AND p.id = 1
WHERE cm.user_id != :userId AND cm.chat_id = :chatId LIMIT 1
""", nativeQuery = true)
    List<Byte[]> getUserAvatar(int chatId, int userId);



    @Query(value = """
SELECT
COALESCE(u.name, '') AS name
FROM chat_member cm
LEFT JOIN "user" u ON cm.user_id = u.pk_user
WHERE cm.user_id != :userId AND cm.chat_id = :chatId LIMIT 1
""", nativeQuery = true)
    List<String> getUserName(int chatId, int userId);

}



