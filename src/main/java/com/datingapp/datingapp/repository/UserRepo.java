package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByName(String name);
    User findByLogin(String login);

    @Query(value = "SELECT u2.name, c.pk_chat " +
            "FROM \"user\" u1 " +
            "JOIN \"chat\" c ON u1.pk_user = c.pk_user OR u1.pk_user = c.pk_user1 " +
            "JOIN \"user\" u2 ON u2.pk_user = CASE " +
                                              " WHEN u1.pk_user = c.pk_user THEN c.pk_user1 " +
                                              " ELSE c.pk_user " +
                                            " END "  +
            "  WHERE u1.pk_user = :user_id", nativeQuery = true)
    List<Object[]> findUsers(@Param("user_id") int pk_user);

}
