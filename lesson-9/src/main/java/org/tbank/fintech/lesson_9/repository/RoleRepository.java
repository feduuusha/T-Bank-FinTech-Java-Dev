package org.tbank.fintech.lesson_9.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tbank.fintech.lesson_9.entity.user.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
