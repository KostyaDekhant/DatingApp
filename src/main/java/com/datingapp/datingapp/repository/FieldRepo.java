package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.ProfileField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FieldRepo extends JpaRepository<ProfileField, Integer> {
    List<ProfileField> findAllByOrderBySortOrderAsc();
}
