package com.calorie.tracker.service;

import com.calorie.tracker.entity.Consumption;
import com.calorie.tracker.entity.Food;
import com.calorie.tracker.entity.User;
import com.calorie.tracker.repository.ConsumptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
class ConsumptionServiceTest {

    @Mock
    private ConsumptionRepository consumptionRepository;

    @Mock
    private UserService userService;

    @Mock
    private FoodService foodService;

    @InjectMocks
    private ConsumptionService consumptionService;

    private User user;
    private Food food;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "encoded", 2000);
        user.setId(1L);
        food = new Food(1L, "Yumurta", 155, 12.6, 1.1, 10.6);
        today = LocalDate.now();
    }

    @Test
    void shouldAddConsumption() {
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(foodService.getFoodById(1L)).thenReturn(food);

        consumptionService.addConsumption("testuser", 1L, today, Consumption.MealType.BREAKFAST, 1.5);

        verify(consumptionRepository).save(any(Consumption.class));
    }

    @Test
    void shouldRejectFutureConsumptionDate() {
        assertThrows(IllegalArgumentException.class,
                () -> consumptionService.addConsumption("testuser", 1L, today.plusDays(1),
                        Consumption.MealType.BREAKFAST, 1.0));
        verifyNoInteractions(userService, foodService, consumptionRepository);
    }

    @Test
    void shouldRejectNonPositiveAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> consumptionService.addConsumption("testuser", 1L, today,
                        Consumption.MealType.BREAKFAST, 0.0));
        verifyNoInteractions(userService, foodService, consumptionRepository);
    }

    @Test
    void shouldDeleteOwnConsumption() {
        Consumption consumption = consumption(10L, user, food, 1.0);
        when(consumptionRepository.findById(10L)).thenReturn(Optional.of(consumption));

        consumptionService.deleteConsumption("testuser", 10L);

        verify(consumptionRepository).delete(consumption);
    }

    @Test
    void shouldRejectDeletingAnotherUsersConsumption() {
        User otherUser = new User("other", "encoded", 2000);
        Consumption consumption = consumption(10L, otherUser, food, 1.0);
        when(consumptionRepository.findById(10L)).thenReturn(Optional.of(consumption));

        assertThrows(AccessDeniedException.class,
                () -> consumptionService.deleteConsumption("testuser", 10L));
        verify(consumptionRepository, never()).delete(any(Consumption.class));
    }

    @Test
    void shouldCalculateDailyCaloriesAndMacros() {
        Consumption first = consumption(1L, user, food, 2.0);
        Food rice = new Food(2L, "Pirinç", 130, 2.7, 28.0, 0.3);
        Consumption second = consumption(2L, user, rice, 1.0);
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(consumptionRepository.findByUserAndDate(user, today)).thenReturn(List.of(first, second));

        assertEquals(440, consumptionService.calculateDailyCalories("testuser", today));
        assertEquals(27.9, consumptionService.calculateDailyProtein("testuser", today), 0.001);
        assertEquals(30.2, consumptionService.calculateDailyCarbs("testuser", today), 0.001);
        assertEquals(21.5, consumptionService.calculateDailyFat("testuser", today), 0.001);
    }

    @Test
    void shouldDetectDailyLimitStatus() {
        user.setDailyCalorieGoal(300);
        Consumption item = consumption(1L, user, food, 2.0);
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(consumptionRepository.findByUserAndDate(user, today)).thenReturn(List.of(item));

        assertTrue(consumptionService.isOverDailyLimit("testuser", today));

        user.setDailyCalorieGoal(400);
        assertFalse(consumptionService.isOverDailyLimit("testuser", today));
    }

    @Test
    void shouldCalculateMonthlyMacroStats() {
        Consumption item = consumption(1L, user, food, 2.0);
        when(userService.findByUsername("testuser")).thenReturn(user);
        when(consumptionRepository.findByUserAndDateBetween(any(User.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(item));

        Map<LocalDate, Map<String, Object>> stats = consumptionService.getMonthlyMacroStats(
                "testuser", today.getYear(), today.getMonthValue());

        assertEquals(310, stats.get(today).get("calories"));
        assertEquals(25.2, (Double) stats.get(today).get("protein"), 0.001);
    }

    private Consumption consumption(Long id, User owner, Food consumedFood, double amount) {
        return new Consumption(id, owner, consumedFood, today, Consumption.MealType.BREAKFAST, amount);
    }
}
