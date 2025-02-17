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
    List<Message> findChatMessages(@Param("chat_id") int pk_chat);
}
