package com.datingapp.datingapp.repository;

import com.datingapp.datingapp.entity.ProfileField;
import com.datingapp.datingapp.entity.UserCompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserCompanyRepo extends JpaRepository<UserCompanyInfo, Integer> {
    Optional<UserCompanyInfo> findByPkUser(int pkUser);

    @Query(value = """
SELECT ucf.pk_user_company_info FROM user_company_info ucf WHERE ucf.pk_user = :pkUser LIMIT 1;
""", nativeQuery=true)
    Integer getPkUserCompanyInfoByPkUser(int pkUser);
}
