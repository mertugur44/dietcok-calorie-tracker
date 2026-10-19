package com.calorie.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.calorie.tracker.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
