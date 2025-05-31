package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Integer> {
    @Query(value = "SELECT COALESCE(MAX(\"pk_chat\"), 0) FROM \"chat\""
            , nativeQuery = true)
    int findMaxPk();

    @Query(value = "SELECT CASE " +
            "WHEN EXISTS (  " +
            "SELECT 1  " +
            "FROM \"chat\" c  " +
            "WHERE c.pk_user = :pk_user AND c.pk_user1 = :pk_user1 " +
               "OR c.pk_user = :pk_user1 AND c.pk_user1 = :pk_user " +
            ")  " +
            "THEN true  " +
            "ELSE false  " +
            "END AS record_exists;" , nativeQuery = true)
    boolean isChatExists(@Param("pk_user") int pk_user, @Param("pk_user1") int pk_user1);


    @Query(value = """
SELECT
    CASE
        WHEN gc.is_group THEN gc.name
        ELSE other_user.name
    END AS chat_name
FROM group_chat gc
LEFT JOIN LATERAL (
    SELECT u.name
    FROM chat_member cm2
    JOIN "user" u ON u.pk_user = cm2.user_id
    WHERE cm2.chat_id = gc.pk_group_chat
      AND cm2.user_id <> :userId
    LIMIT 1
) other_user ON TRUE
WHERE gc.pk_group_chat = :chatId;
""", nativeQuery = true)
    String getName(int chatId, int userId);
}
