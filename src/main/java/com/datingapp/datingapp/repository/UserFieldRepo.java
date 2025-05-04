package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.ProfileField;
import com.datingapp.datingapp.entity.UserProfileField;
import com.datingapp.datingapp.entity.UserProfileFieldId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFieldRepo extends JpaRepository<UserProfileField, UserProfileFieldId> {

    List<UserProfileField> findByIdUserId(int userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserProfileField upf WHERE upf.id.userId = :userId")
    void deleteByUserId(int userId);
}
