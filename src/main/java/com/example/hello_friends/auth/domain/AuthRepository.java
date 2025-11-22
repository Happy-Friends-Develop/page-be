package com.example.hello_friends.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);
}
