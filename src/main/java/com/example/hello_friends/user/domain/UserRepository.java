package com.example.hello_friends.user.domain;

import com.example.hello_friends.common.entity.EntityState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByAuthId(Long authId);
    Optional<User> findByIdAndState(Long id, EntityState state);
    List<User> findAllByState(EntityState state);
    List<User> findAllByUserRole(UserRole userRole);
}
