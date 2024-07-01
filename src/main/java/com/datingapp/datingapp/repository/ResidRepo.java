package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.enitity.Residence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ResidRepo extends JpaRepository<Residence, Integer> {
    Residence findByCity(String city);
    @Query("SELECT r FROM Residence r WHERE r.pk_user = :value")
    Residence findByPk_user(@Param("value") int pk_user);
}
