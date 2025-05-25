package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.Dislike;
import com.datingapp.datingapp.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DislikeRepo extends JpaRepository<Dislike, Integer> {

}
