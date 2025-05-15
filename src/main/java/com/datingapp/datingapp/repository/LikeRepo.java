package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.Like;
import com.datingapp.datingapp.entity.LikeDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepo extends JpaRepository<Like, Integer> {
    @Query(value = "SELECT COALESCE(MAX(\"pk_like\"), 0) FROM \"like\""
            , nativeQuery = true)
    int findMaxPk();
    @Query(value = "SELECT l.poster, l.time, u.name, p.image, u.birthday " +
            "FROM \"like\" l " +
            "JOIN \"user\" u ON u.pk_user = l.poster " +
            "LEFT JOIN \"user_pic\" up ON u.pk_user = up.pk_user " +
            "LEFT JOIN \"picture\" p ON p.pk_picture = (  " +
            "    SELECT MIN(pk_picture) " +
            "    FROM \"user_pic\" " +
            "    WHERE up.pk_user = u.pk_user  " +
            ") " +
            "WHERE l.liker = :id " +
            "GROUP BY l.poster, l.time, u.name, p.image, u.birthday;"
            , nativeQuery = true)
    List<Object[]> findByLiker(@Param("id") int user_id);

    @Query(value = """
SELECT l.liker, l.time, u.name, p.image, u.birthday
FROM "like" l
JOIN "user" u ON u.pk_user = l.liker
LEFT JOIN "user_pic" up ON u.pk_user = up.pk_user
LEFT JOIN "picture" p ON p.pk_picture = up.pk_picture AND p.id = 1
WHERE l.poster = :id
GROUP BY l.liker, l.time, u.name, p.image, u.birthday;
""", nativeQuery = true)
    List<Object[]> findByReceiver(@Param("id") int user_id); //LikeDTO

    @Transactional
    @Modifying
    @Query(value = """
DELETE FROM \"like\" l WHERE l.liker = :liker 
AND l.poster = :poster 
""" , nativeQuery = true)
    int deleteLike(@Param("liker") int liker, @Param("poster") int poster);

    @Query(value = "SELECT CASE" +
            " WHEN EXISTS ( " +
            "\t SELECT 1 " +
            "\t FROM \"like\" l  " +
            "\t WHERE l.liker = :liker AND l.poster = :poster" +
            "\t ) " +
            "\t THEN true " +
            "\t ELSE false " +
            "END AS record_exists;" , nativeQuery = true)
    boolean isLikeExists(@Param("liker") int liker, @Param("poster") int poster);

}