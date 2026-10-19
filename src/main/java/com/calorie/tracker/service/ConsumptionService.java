package com.calorie.tracker.service;

import com.calorie.tracker.entity.Consumption;
import com.calorie.tracker.entity.Food;
import com.calorie.tracker.entity.User;
import com.calorie.tracker.repository.ConsumptionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConsumptionService {

    private final ConsumptionRepository consumptionRepository;
    private final UserService userService;
    private final FoodService foodService;

    public ConsumptionService(ConsumptionRepository consumptionRepository, UserService userService,
            FoodService foodService) {
        this.consumptionRepository = consumptionRepository;
        this.userService = userService;
        this.foodService = foodService;
    }

    @Transactional
    public void addConsumption(String username, Long foodId, LocalDate date, Consumption.MealType mealType,
            Double amount) {
        validateConsumptionInput(date, mealType, amount);

        User user = userService.findByUsername(username);
        Food food = foodService.getFoodById(foodId);

        Consumption consumption = new Consumption();
        consumption.setUser(user);
        consumption.setFood(food);
        consumption.setDate(date);
        consumption.setMealType(mealType);
        consumption.setAmount(amount);

        consumptionRepository.save(consumption);
    }

    @Transactional
    public void deleteConsumption(String username, Long id) {
        Consumption consumption = consumptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tüketim kaydı bulunamadı."));

        if (!consumption.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Bu kayıt üzerinde işlem yapma yetkiniz yok.");
        }

        consumptionRepository.delete(consumption);
    }

    public List<Consumption> getDailyConsumptions(String username, LocalDate date) {
        User user = userService.findByUsername(username);

        List<Consumption> consumptions = new ArrayList<>(
                consumptionRepository.findByUserAndDate(user, date)
        );

        consumptions.sort(Comparator.comparing(Consumption::getMealType));
        return consumptions;
    }

    public int calculateDailyCalories(String username, LocalDate date) {
        return getDailyConsumptions(username, date).stream()
                .mapToInt(this::calculateCalories)
                .sum();
    }

    public double calculateDailyProtein(String username, LocalDate date) {
        return getDailyConsumptions(username, date).stream()
                .mapToDouble(c -> valueOrZero(c.getFood().getProtein()) * c.getAmount())
                .sum();
    }

    public double calculateDailyCarbs(String username, LocalDate date) {
        return getDailyConsumptions(username, date).stream()
                .mapToDouble(c -> valueOrZero(c.getFood().getCarbs()) * c.getAmount())
                .sum();
    }

    public double calculateDailyFat(String username, LocalDate date) {
        return getDailyConsumptions(username, date).stream()
                .mapToDouble(c -> valueOrZero(c.getFood().getFat()) * c.getAmount())
                .sum();
    }

    public boolean isOverDailyLimit(String username, LocalDate date) {
        User user = userService.findByUsername(username);
        return calculateDailyCalories(username, date) > user.getDailyCalorieGoal();
    }

    public Map<LocalDate, Map<String, Object>> getMonthlyMacroStats(String username, int year, int month) {
        User user = userService.findByUsername(username);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Consumption> monthlyConsumptions = consumptionRepository.findByUserAndDateBetween(user, startDate,
                endDate);
        Map<LocalDate, Map<String, Object>> stats = new HashMap<>();

        for (Consumption consumption : monthlyConsumptions) {
            Map<String, Object> dayStats = stats.computeIfAbsent(consumption.getDate(), ignored -> new HashMap<>());

            double protein = valueOrZero(consumption.getFood().getProtein()) * consumption.getAmount();
            double carbs = valueOrZero(consumption.getFood().getCarbs()) * consumption.getAmount();
            double fat = valueOrZero(consumption.getFood().getFat()) * consumption.getAmount();
            int calories = calculateCalories(consumption);

            dayStats.merge("protein", protein, (a, b) -> (double) a + (double) b);
            dayStats.merge("carbs", carbs, (a, b) -> (double) a + (double) b);
            dayStats.merge("fat", fat, (a, b) -> (double) a + (double) b);
            dayStats.merge("calories", calories, (a, b) -> (int) a + (int) b);
        }

        return stats;
    }

    private void validateConsumptionInput(LocalDate date, Consumption.MealType mealType, Double amount) {
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Gelecek tarih için tüketim kaydı eklenemez.");
        }

        if (mealType == null) {
            throw new IllegalArgumentException("Öğün tipi seçilmelidir.");
        }

        if (amount == null || !Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Miktar 0'dan büyük olmalıdır.");
        }
    }

    private int calculateCalories(Consumption consumption) {
        return (int) Math.round(consumption.getFood().getCalories() * consumption.getAmount());
    }

    private double valueOrZero(Double value) {
        return value == null ? 0.0 : value;
    }
}
