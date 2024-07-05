package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface  PicRepo extends JpaRepository<Picture, Integer> {

}
