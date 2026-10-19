package com.calorie.tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.calorie.tracker.entity.Consumption;
import com.calorie.tracker.entity.User;
import java.time.LocalDate;
import java.util.List;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    List<Consumption> findByUserAndDate(User user, LocalDate date);

    List<Consumption> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
