package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.UserPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPicRepo extends JpaRepository<UserPic, Integer> {

}
