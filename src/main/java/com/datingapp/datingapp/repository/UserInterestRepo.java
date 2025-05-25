package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserInterestRepo extends JpaRepository<UserInterest, Integer> {

    @Query(value = """
SELECT * FROM user_interest ui WHERE ui.pk_user = :id ORDER BY ui.weight DESC
""", nativeQuery = true)
    List<UserInterest> findUserInterestByPkUser(int id);
}
