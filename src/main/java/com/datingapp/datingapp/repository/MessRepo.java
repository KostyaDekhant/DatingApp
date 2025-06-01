package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessRepo extends JpaRepository<Message, Integer>{
    @Query(value = "SELECT m.pk_message, m.message, m.time, m.pk_user, m.pk_chat " + //
            /*"CASE " +
            "WHEN m.pk_user = c.pk_user THEN c.pk_user1 " +
            "WHEN m.pk_user = c.pk_user1 THEN c.pk_user " +
            "END as pk_user2 " +*/
            "FROM \"message\" m INNER JOIN \"chat\" c ON m.pk_chat = c.pk_chat " +
            "WHERE m.pk_chat = :chat_id", nativeQuery = true)
    List<Message> findChatMessagesOld(@Param("chat_id") int pk_chat);


    @Query(value = """
SELECT m.pk_message, m.message, m.time, m.pk_user, m.pk_chat
FROM "message" m INNER JOIN "group_chat" c ON m.pk_chat = c.pk_group_chat
WHERE m.pk_chat = :chatId
ORDER BY m.time DESC
LIMIT :limit OFFSET :offset;
""", nativeQuery = true)
    List<Message> findChatMessages(@Param("chatId") int chatId,
                                   @Param("limit") int limit,
                                   @Param("offset") int offset);

    @Query(value= """
SELECT *
FROM message m
WHERE pk_chat = :chatId
ORDER BY time DESC
LIMIT 1;
""", nativeQuery = true)
    Message getLastMessage(int chatId);

    Message getMessageByPkMessage(Integer integer);

    @Query(value= """
   SELECT m.*
   FROM message m
   LEFT JOIN message_read mr
     ON m.pk_message = mr.message_id AND mr.user_id = :userId
   WHERE m.pk_chat = :chatId
     AND mr.message_id IS NULL
    AND m.pk_user != :userId
   ORDER BY m.time DESC; 
""", nativeQuery = true)
    List<Message> getUnreadMessages(int chatId, int userId);

    @Query(value= """
SELECT m.*
FROM message m
WHERE m.pk_chat = :chatId
  AND m.pk_user = :userId
  AND NOT EXISTS (
      SELECT 1
      FROM message_read mr
      JOIN chat_member cm ON cm.user_id = mr.user_id AND cm.chat_id = m.pk_chat
      WHERE mr.message_id = m.pk_message
        AND mr.user_id != m.pk_user -- Исключаем отправителя
  )
ORDER BY m.time DESC;
""", nativeQuery = true)
    List<Message> getAnotherUnreadMessages(int chatId, int userId);

    @Query(value= """
SELECT COUNT(*) AS unread_count
FROM message m
LEFT JOIN message_read mr
  ON m.pk_message = mr.message_id AND mr.user_id = :userId
WHERE m.pk_chat = :chatId
  AND mr.message_id IS NULL
  AND m.pk_user != :userId;
""", nativeQuery = true)
    Integer getCountOfUnreadMessages(Integer chatId, Integer userId);



    boolean existsByPkMessageAndPkUser(int messageId, int userId);
}
