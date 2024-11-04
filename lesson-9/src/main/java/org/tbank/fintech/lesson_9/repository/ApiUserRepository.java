package org.tbank.fintech.lesson_9.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tbank.fintech.lesson_9.entity.user.ApiUser;

import java.util.Optional;

public interface ApiUserRepository extends JpaRepository<ApiUser, Long> {

    Optional<ApiUser> findByUsername(String username);
    Optional<ApiUser> findByEmail(String email);
}
