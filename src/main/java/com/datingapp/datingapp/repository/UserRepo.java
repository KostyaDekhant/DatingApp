package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.ChatDTO;
import com.datingapp.datingapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    @Query(value = """
SELECT u.login FROM "user" u WHERE u.pk_user = :id
""", nativeQuery = true)
    List<String> getLoginByPkUser(Integer id);

    User findByName(String name);
    Optional<User> findByLogin(String login);

    @Query(value = """
SELECT
  u2.name      AS partner_name,
  c.pk_chat    AS chat_id,
  m.message    AS last_message,
  CASE
    WHEN c.pk_user = :userId THEN c.pk_user1
    ELSE c.pk_user
  END          AS partner_id,
  p.image      AS avatar
FROM "chat" c
  -- сначала все JOIN-ы
  JOIN "user" u2
    ON u2.pk_user = CASE
                       WHEN c.pk_user = :userId THEN c.pk_user1
                       ELSE c.pk_user
                    END
  LEFT JOIN (
    SELECT m1.pk_chat, m1.pk_user, m1.message
    FROM "message" m1
    WHERE m1.time = (
      SELECT MAX(m2.time)
      FROM "message" m2
      WHERE m2.pk_chat = m1.pk_chat
    )
  ) m
    ON m.pk_chat = c.pk_chat
  LEFT JOIN "user_pic" up
    ON up.pk_user = u2.pk_user
  LEFT JOIN "picture" p
    ON up.pk_picture = p.pk_picture
       AND p.id = 1
-- а потом уже WHERE
WHERE :userId IN (c.pk_user, c.pk_user1)

""",
            nativeQuery = true)
    List<Object[]> findChatPartners(@Param("userId") int userId);

    @Query(value = """
SELECT DISTINCT ON (u2.pk_user)
     u2.name        AS partner_name,
     u2.pk_user     AS partner_id,
     p.image        AS avatar
FROM "user" u2
LEFT JOIN user_pic up
  ON up.pk_user = u2.pk_user
LEFT JOIN picture p
  ON p.pk_picture = up.pk_picture
 AND p.id = 1
WHERE
  u2.pk_user != :userId
  AND NOT EXISTS (
    SELECT 1
    FROM chat_member cm
    WHERE cm.user_id = u2.pk_user
      AND cm.chat_id = :chatId
  )
  AND EXISTS (
    SELECT 1
    FROM chat_member cm1
    JOIN chat_member cm2
      ON cm1.chat_id = cm2.chat_id
    JOIN group_chat gc
      ON gc.pk_group_chat = cm1.chat_id
     AND gc.is_group = FALSE
    WHERE
      cm1.user_id = :userId
      AND cm2.user_id = u2.pk_user
  )
ORDER BY
  u2.pk_user,
  (p.image IS NULL);
""", nativeQuery = true)
    List<Object[]> findGroupChatPartners(@Param("userId") int userId, @Param("chatId") int chatId);


    @Query(value = """
SELECT
  u2.name      AS partner_name,
  c.pk_chat    AS chat_id,
  m.message    AS last_message,
  CASE
    WHEN c.pk_user = :userId THEN c.pk_user1
    ELSE c.pk_user
  END          AS partner_id,
  p.image      AS avatar
FROM "chat" c
  -- сначала все JOIN-ы
  JOIN "user" u2
    ON u2.pk_user = CASE
                       WHEN c.pk_user = :userId THEN c.pk_user1
                       ELSE c.pk_user
                    END
  LEFT JOIN (
    SELECT m1.pk_chat, m1.pk_user, m1.message
    FROM "message" m1
    WHERE m1.time = (
      SELECT MAX(m2.time)
      FROM "message" m2
      WHERE m2.pk_chat = m1.pk_chat
    )
  ) m
    ON m.pk_chat = c.pk_chat
  LEFT JOIN "user_pic" up
    ON up.pk_user = u2.pk_user
  LEFT JOIN "picture" p
    ON up.pk_picture = p.pk_picture
       AND p.id = 1
-- а потом уже WHERE
WHERE :chatId = c.pk_chat

""",
            nativeQuery = true)
    List<Object[]> findChat(@Param("userId") int userId, @Param("chatId") int chatId);

//    @Query(value = """
//SELECT
//  u2.name      AS partner_name,
//  c.pk_chat    AS chat_id,
//  m.message    AS last_message,
//  CASE
//    WHEN c.pk_user = :userId THEN c.pk_user1
//    ELSE c.pk_user
//  END          AS partner_id,
//  p.image      AS avatar
//FROM "chat" c
//  -- сначала все JOIN-ы
//  JOIN "user" u2
//    ON u2.pk_user = CASE
//                       WHEN c.pk_user = :userId THEN c.pk_user1
//                       ELSE c.pk_user
//                    END
//  LEFT JOIN (
//    SELECT m1.pk_chat, m1.pk_user, m1.message
//    FROM "message" m1
//    WHERE m1.time = (
//      SELECT MAX(m2.time)
//      FROM "message" m2
//      WHERE m2.pk_chat = m1.pk_chat
//    )
//  ) m
//    ON m.pk_chat = c.pk_chat
//  LEFT JOIN "user_pic" up
//    ON up.pk_user = u2.pk_user
//  LEFT JOIN "picture" p
//    ON up.pk_picture = p.pk_picture
//       AND p.id = 1
//-- а потом уже WHERE
//WHERE c.pk_chat = :chatID LIMIT 1
//
//""",
//            nativeQuery = true)
//    Object[] findChatPartnerById(@Param("chatId") int chatId, @Param("userId") int userId);


    @Query(value = """
            WITH     OrderedUsers AS ( 
                SELECT u.name, u.birthday, u.gender, u.height, u.description, u.pk_user,  
                       ROW_NUMBER() OVER (ORDER BY u.pk_user ASC) AS RowNum  
                FROM "user" u  
                WHERE u.pk_user <> :user_id  
            )  
            SELECT pk_user, name, birthday, gender, height, description  
            FROM OrderedUsers  
            WHERE RowNum = COALESCE(  
                (SELECT MIN(RowNum) FROM OrderedUsers WHERE pk_user > :prev_user_id),  
                (SELECT MIN(RowNum) FROM OrderedUsers))
            """, nativeQuery = true)
    List<Object[]> findQuestUsers1(@Param("user_id") int pk_user,
                                  @Param("prev_user_id") int prev_pk_user);


    @Query(value = """  
            SELECT u.pk_user, u.name, u.birthday, u.gender, u.height, u.description  
            FROM "user" u  
            WHERE u.pk_user = :user_id;
            """, nativeQuery = true)
    List<Object[]> findQuestUsersById(@Param("user_id") int pk_user);




    @Query(value = """
SELECT u.pk_user,
       COALESCE(SUM(mi.weight * ui.weight),0) AS score
FROM "user" u
  LEFT JOIN user_interest ui
    ON ui.pk_user = :user_id
  -- джоин по интересам кандидата
  LEFT JOIN user_interest mi
    ON mi.pk_interest = ui.pk_interest
   AND mi.pk_user = u.pk_user

WHERE u.pk_user <> :user_id
  AND date_part('year', age(current_date, u.birthday))::INT
      BETWEEN :age_min AND :age_max
  AND u.height BETWEEN :height_min AND :height_max
  AND (
       :gender = 'Both'      -- если параметр Both — не фильтруем по полу
    OR u.gender = :gender    -- иначе оставляем только совпадающий
  )
  -- нет dislike от нас к этому пользователю
  AND NOT EXISTS (
    SELECT 1
    FROM dislike d
    WHERE d.disliker = :user_id
      AND d.poster   = u.pk_user
  )
  AND NOT EXISTS (
    SELECT 1
    FROM "like" d
    WHERE d.liker = :user_id
      AND d.poster   = u.pk_user
  )

GROUP BY u.pk_user, u.birthday, u.height
HAVING COALESCE(SUM(mi.weight * ui.weight),0) >= 0   -- только с ненулевым совпадением
ORDER BY score DESC
LIMIT :limit OFFSET :offset;    
    """, nativeQuery = true)
    List<Object[]> findQuestUsers(@Param("user_id") int user_id,
                                  @Param("age_min") int age_min,
                                  @Param("age_max") int age_max,
                                  @Param("height_min") int height_min,
                                  @Param("height_max") int height_max,
                                  @Param("gender") String gender,
                                  @Param("limit") int limit,
                                  @Param("offset") int offset);

}
