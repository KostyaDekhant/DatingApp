package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.User_pic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPicRepo extends JpaRepository<User_pic, Integer> {

}
