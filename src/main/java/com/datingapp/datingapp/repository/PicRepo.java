package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.Picture;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = "SELECT p.pk_picture, p.id, p.image " +
            "FROM \"picture\" p " +
            "INNER JOIN \"user_pic\" up " +
            "ON up.pk_picture = p.pk_picture " +
            "WHERE up.pk_user = :id"
            , nativeQuery = true)
    List<Object[]> findByUserId(@Param("id") int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM \"user_pic\" u " +
            "WHERE u.pk_picture = :id; " +
            "DELETE FROM \"picture\" p " +
            "WHERE p.pk_picture = :id", nativeQuery = true)
    int deleteImage(@Param("id") int id);

    @Transactional
    @Modifying
    @Query(value = "WITH OrderedPictures AS ( " +
            "    SELECT p.pk_picture, p.id, " +
            "           ROW_NUMBER() OVER (ORDER BY p.id ASC) as new_id " +
            "    FROM \"picture\" p " +
            "    INNER JOIN \"user_pic\" up ON p.pk_picture = up.pk_picture " +
            "    WHERE up.pk_user = :user_id " +
            ") " +
            "UPDATE \"picture\" p " +
            "SET id = op.new_id  " +
            "FROM OrderedPictures op " +
            "WHERE p.pk_picture = op.pk_picture", nativeQuery = true)
    void updateId(@Param("user_id") int user_id);


    @Query(value = "SELECT u.pk_user FROM \"user_pic\" up INNER JOIN " +
            "\"user\" u ON up.pk_user = u.pk_user WHERE up.pk_picture = :image_id LIMIT 1"
            , nativeQuery = true)
    int findUserById(@Param("image_id") int id);
}
