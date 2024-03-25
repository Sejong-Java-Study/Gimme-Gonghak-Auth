package com.example.gimmegonghakauth.dao;

import com.example.gimmegonghakauth.domain.UserDomain;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<UserDomain,Long> {

    Optional<UserDomain> findByStudentId(Long studentId);

    boolean existsByStudentId(Long studentId);

    boolean existsByEmail(String email);

}
