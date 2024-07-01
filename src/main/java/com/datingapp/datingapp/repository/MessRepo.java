package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.ChatMessageDto;
import com.datingapp.datingapp.enitity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessRepo extends JpaRepository<Message, Integer> {
    @Query(value = "SELECT m.message, m.pk_user, " +
            "CASE " +
            "WHEN m.pk_user = c.pk_user THEN c.pk_user1 " +
            "WHEN m.pk_user = c.pk_user1 THEN c.pk_user " +
            "END as pk_user2 " +
            "FROM Message m INNER JOIN Chat c ON m.pk_chat = c.pk_chat " +
            "WHERE m.pk_chat = :chat_id", nativeQuery = true)
    List<ChatMessageDto> findChatMessages(@Param("chat_id") int pk_chat);
}
