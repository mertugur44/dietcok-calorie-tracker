package com.calorie.tracker.repository;

import com.calorie.tracker.entity.Food;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class FoodRepositoryTest {

    @Autowired
    private FoodRepository foodRepository;

    @Test
    void shouldPersistAndReadFood() {
        Food food = new Food(null, "Apple", 52, 0.3, 14.0, 0.2);
        Food saved = foodRepository.save(food);

        assertThat(saved.getId()).isNotNull();
        assertThat(foodRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Food::getName)
                .isEqualTo("Apple");
    }
}
