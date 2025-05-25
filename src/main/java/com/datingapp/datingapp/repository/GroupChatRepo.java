package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.GroupChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
lm.pk_message              AS last_message,
  COALESCE(lm.time, gc.created_at)            AS last_time
FROM
chat_member cm
  -- нужные нам чаты
JOIN group_chat gc
ON cm.chat_id = gc.pk_group_chat
AND cm.user_id = :userId

  -- LATERAL-джоин: для каждой строки вытягиваем последнее сообщение
LEFT JOIN LATERAL (
        SELECT m.pk_message, m.time
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
last_time DESC  -- сортируем чаты от самых «свежих»
LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<Object[]> getChatsInfo(int userId, int limit, int offset);



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

    @Query(value = """
WITH private_photos AS (
  SELECT
    cm.chat_id,
    p.image       AS avatar,
    ROW_NUMBER() OVER (
      PARTITION BY cm.chat_id
      ORDER BY up.pk_picture
    ) AS rn
  FROM chat_member cm
  JOIN group_chat gc
    ON gc.pk_group_chat = cm.chat_id
   AND gc.is_group     = FALSE
  LEFT JOIN user_pic up
    ON up.pk_user = cm.user_id
  LEFT JOIN picture p
    ON p.pk_picture = up.pk_picture
   AND p.id = 1
  WHERE
    cm.user_id <> :userId
    AND cm.chat_id IN (:chatIds)
)
SELECT
  gc.pk_group_chat AS chatId,
  gc.image         AS avatar
FROM group_chat gc
WHERE
  gc.is_group = TRUE
  AND gc.pk_group_chat IN (:chatIds)

UNION ALL

SELECT
  chat_id   AS chatId,
  avatar
FROM private_photos
WHERE rn = 1
""", nativeQuery = true)
    List<Object[]> findAvatars(
            @Param("chatIds") List<Integer> chatIds,
            @Param("userId")  int userId
    );



    @Query(value = """
SELECT 
       created_by,
       created_at,
       is_group
FROM group_chat gr 
WHERE gr.pk_group_chat = :chatId
""", nativeQuery = true)
    List<Object[]> getGroupChatInfoById(int chatId);

    GroupChat findGroupChatByPkGroupChat(int chatId);

    @Modifying
    @Query(value = """
update group_chat set name = :name where :name is not null and pk_group_chat = :chatId and created_by = :userId;
""", nativeQuery = true)
    void updateGroupChatName(int chatId, int userId, String name);

    @Modifying
    @Query(value = """
update group_chat set image = :image where pk_group_chat = :chatId and created_by = :userId;
""", nativeQuery = true)
    void updateGroupChatImage(int chatId, int userId, byte[] image);


    @Modifying
    @Query(value = """
SELECT
gc.pk_group_chat       AS chat_id,
        -- если это групповая — показываем её название,
        -- иначе — берём имя единственного другого участника
        CASE
WHEN gc.is_group THEN gc.name
ELSE other_user.name
END                     AS chat_name,
lm.pk_message              AS last_message,
  COALESCE(lm.time, gc.created_at)            AS last_time
FROM
chat_member cm
  -- нужные нам чаты
JOIN group_chat gc
ON cm.chat_id = gc.pk_group_chat
AND cm.chat_id = :chatId

  -- LATERAL-джоин: для каждой строки вытягиваем последнее сообщение
LEFT JOIN LATERAL (
        SELECT m.pk_message, m.time
                FROM message m
                WHERE m.pk_chat = gc.pk_group_chat
                ORDER BY m.time DESC
                LIMIT 1
) lm ON TRUE

LEFT JOIN LATERAL (
        SELECT u.name
                FROM chat_member cm2
                JOIN "user" u
                ON u.pk_user = cm2.user_id
                WHERE cm2.chat_id  = gc.pk_group_chat
                AND cm2.user_id <> :userId
                LIMIT 1
) other_user ON TRUE                
""", nativeQuery = true)
    List<Object[]> getGroupChatByID(int chatId, int userId);
}


