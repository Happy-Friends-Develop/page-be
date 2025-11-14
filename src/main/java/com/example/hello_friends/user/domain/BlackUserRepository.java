package com.example.hello_friends.user.domain;

import com.example.hello_friends.common.entity.EntityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackUserRepository extends JpaRepository<BlackUser, Long> {
    boolean existsByUserAndState(User user, EntityState state);
    boolean existsByUser_EmailAndState(String email, EntityState state);
    boolean existsByUser_PhoneAndState(String phone, EntityState state);
    Optional<BlackUser> findByUserAndState(User user, EntityState state);
}
