package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepo extends JpaRepository<Like, Integer> {
    @Query(value = "SELECT COALESCE(MAX(\"pk_like\"), 0) FROM \"like\""
            , nativeQuery = true)
    int findMaxPk();
    @Query(value = "SELECT l.poster, l.time " +
            "FROM \"like\" l  " +
            "WHERE l.liker = :id"
            , nativeQuery = true)
    List<Object[]> findByLiker(@Param("id") int user_id);

    @Query(value = "SELECT l.liker, l.time " +
            "FROM \"like\" l  " +
            "WHERE l.poster = :id"
            , nativeQuery = true)
    List<Object[]> findByReceiver(@Param("id") int user_id);
}
