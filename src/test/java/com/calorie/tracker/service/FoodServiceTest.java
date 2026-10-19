package com.calorie.tracker.service;

import com.calorie.tracker.entity.Food;
import com.calorie.tracker.repository.FoodRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private FoodService foodService;

    @Test
    void shouldReturnAllFoods() {
        when(foodRepository.findAll()).thenReturn(List.of(new Food(), new Food()));

        assertEquals(2, foodService.getAllFoods().size());
        verify(foodRepository).findAll();
    }

    @Test
    void shouldReturnFoodById() {
        Food apple = new Food(1L, "Apple", 52, 0.3, 13.8, 0.2);
        when(foodRepository.findById(1L)).thenReturn(Optional.of(apple));

        assertEquals(apple, foodService.getFoodById(1L));
    }

    @Test
    void shouldFailWhenFoodDoesNotExist() {
        when(foodRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> foodService.getFoodById(99L));
        assertEquals("Besin bulunamadı.", exception.getMessage());
    }
}
