package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface  PicRepo extends JpaRepository<Picture, Integer> {
    @Query(value = "SELECT COALESCE(MAX(\"pk_picture\"), 0) FROM \"picture\""
            , nativeQuery = true)
    int findMaxPk();
    @Query(value = "SELECT * FROM \"picture\" p WHERE p.pk_picture = :id"
            , nativeQuery = true)
    Picture findById(@Param("id") int id);
    @Query(value = "SELECT p.pk_picture, p.id, p.image\n" +
            "FROM \"picture\" p \n" +
            "INNER JOIN \"user_pic\" up\n" +
            "ON up.pk_picture = p.pk_picture \n" +
            "WHERE up.pk_user = :id"
            , nativeQuery = true)
    List<Object[]> findByUserId(@Param("id") int id);
}
