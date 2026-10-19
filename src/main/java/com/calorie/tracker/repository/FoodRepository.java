package com.calorie.tracker.repository;

import com.calorie.tracker.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
