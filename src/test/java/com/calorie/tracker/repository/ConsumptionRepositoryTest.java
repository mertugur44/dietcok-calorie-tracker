package com.calorie.tracker.repository;

import com.calorie.tracker.entity.Consumption;
import com.calorie.tracker.entity.Food;
import com.calorie.tracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ConsumptionRepositoryTest {

    @Autowired
    private ConsumptionRepository consumptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Test
    public void testFindConsumption() {
        User user = new User("user1", "pass", 2000);
        userRepository.save(user);

        Food food = new Food(null, "Banana", 89, 1.1, 22.8, 0.3);
        foodRepository.save(food);

        Consumption c = new Consumption(null, user, food, LocalDate.now(), Consumption.MealType.LUNCH, 1.0);
        consumptionRepository.save(c);

        List<Consumption> found = consumptionRepository.findByUserAndDate(user, LocalDate.now());
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFood().getName()).isEqualTo("Banana");
    }
}
