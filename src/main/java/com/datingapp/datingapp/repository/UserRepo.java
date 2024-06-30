package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByName(String name);
}
