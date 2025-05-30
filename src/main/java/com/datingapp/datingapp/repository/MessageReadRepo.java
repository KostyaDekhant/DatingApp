package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.MessageRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageReadRepo extends JpaRepository<MessageRead, Integer> {
    //boolean existsByMessage_IdAndUser_Id(int messageId, int userId);
}
