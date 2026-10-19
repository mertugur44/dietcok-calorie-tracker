package com.calorie.tracker.repository;

import com.calorie.tracker.entity.User;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Tag("repository")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        User user = new User("testuser", "password", 2000);
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser");
        assertThat(found).isPresent();
        assertThat(found.get().getDailyCalorieGoal()).isEqualTo(2000);
    }
}
