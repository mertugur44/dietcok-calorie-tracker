package com.calorie.tracker.controller;

import com.calorie.tracker.entity.Consumption;
import com.calorie.tracker.entity.User;
import com.calorie.tracker.service.ConsumptionService;
import com.calorie.tracker.service.FoodService;
import com.calorie.tracker.service.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class DashboardController {

    private final ConsumptionService consumptionService;
    private final FoodService foodService;
    private final UserService userService;

    public DashboardController(ConsumptionService consumptionService, FoodService foodService,
            UserService userService) {
        this.consumptionService = consumptionService;
        this.foodService = foodService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            Model model) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username);

        LocalDate now = LocalDate.now();
        int targetYear = year != null ? year : now.getYear();
        int targetMonth = month != null ? month : now.getMonthValue();
        YearMonth yearMonth = YearMonth.of(targetYear, targetMonth);

        Map<LocalDate, Map<String, Object>> monthlyStats = consumptionService
                .getMonthlyMacroStats(username, targetYear, targetMonth);

        List<List<CalendarDay>> weeks = buildCalendar(yearMonth, monthlyStats, user);

        Locale trLocale = Locale.forLanguageTag("tr-TR");
        String monthNameTr = yearMonth.getMonth().getDisplayName(TextStyle.FULL, trLocale);

        model.addAttribute("username", username);
        model.addAttribute("currentMonthName", monthNameTr.toUpperCase(trLocale));
        model.addAttribute("currentYear", targetYear);
        model.addAttribute("weeks", weeks);
        model.addAttribute("user", user);
        model.addAttribute("prevMonth", targetMonth == 1 ? 12 : targetMonth - 1);
        model.addAttribute("prevYear", targetMonth == 1 ? targetYear - 1 : targetYear);
        model.addAttribute("nextMonth", targetMonth == 12 ? 1 : targetMonth + 1);
        model.addAttribute("nextYear", targetMonth == 12 ? targetYear + 1 : targetYear);

        return "dashboard";
    }

    @GetMapping("/day/{date}")
    public String dailyView(@PathVariable String date,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        LocalDate localDate = LocalDate.parse(date);
        String username = userDetails.getUsername();

        List<Consumption> consumptions = consumptionService.getDailyConsumptions(username, localDate);
        Map<Consumption.MealType, List<Consumption>> groupedConsumptions = createMealGroups(consumptions);

        Map<Consumption.MealType, Integer> mealTotals = new HashMap<>();
        for (Map.Entry<Consumption.MealType, List<Consumption>> entry : groupedConsumptions.entrySet()) {
            int sum = entry.getValue().stream()
                    .mapToInt(c -> (int) Math.round(c.getFood().getCalories() * c.getAmount()))
                    .sum();
            mealTotals.put(entry.getKey(), sum);
        }

        model.addAttribute("date", localDate);
        model.addAttribute("groupedConsumptions", groupedConsumptions);
        model.addAttribute("mealTotals", mealTotals);
        model.addAttribute("totalCalories", consumptionService.calculateDailyCalories(username, localDate));
        model.addAttribute("totalProtein", String.format("%.1f", consumptionService.calculateDailyProtein(username, localDate)));
        model.addAttribute("totalCarbs", String.format("%.1f", consumptionService.calculateDailyCarbs(username, localDate)));
        model.addAttribute("totalFat", String.format("%.1f", consumptionService.calculateDailyFat(username, localDate)));
        model.addAttribute("isOverLimit", consumptionService.isOverDailyLimit(username, localDate));
        model.addAttribute("foods", foodService.getAllFoods());

        return "daily-view";
    }

    @PostMapping("/consumption/add")
    public String addConsumption(@RequestParam String date,
            @RequestParam Long foodId,
            @RequestParam Consumption.MealType mealType,
            @RequestParam Double amount,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        LocalDate localDate = LocalDate.parse(date);
        try {
            consumptionService.addConsumption(userDetails.getUsername(), foodId, localDate, mealType, amount);
            redirectAttributes.addFlashAttribute("success", "Besin kaydı eklendi.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/day/" + localDate;
    }

    @PostMapping("/consumption/{id}/delete")
    public String deleteConsumption(@PathVariable Long id,
            @RequestParam String date,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        LocalDate localDate = LocalDate.parse(date);
        try {
            consumptionService.deleteConsumption(userDetails.getUsername(), id);
            redirectAttributes.addFlashAttribute("success", "Besin kaydı silindi.");
        } catch (IllegalArgumentException | AccessDeniedException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/day/" + localDate;
    }

    private List<List<CalendarDay>> buildCalendar(YearMonth yearMonth,
            Map<LocalDate, Map<String, Object>> monthlyStats, User user) {
        List<List<CalendarDay>> weeks = new ArrayList<>();
        List<CalendarDay> currentWeek = new ArrayList<>();

        int firstDayOfWeek = yearMonth.atDay(1).getDayOfWeek().getValue();
        for (int i = 1; i < firstDayOfWeek; i++) {
            currentWeek.add(null);
        }

        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            Map<String, Object> dayStats = monthlyStats.get(date);

            String status = "no-data";
            Integer totalCalories = null;
            double protein = 0.0;
            double carbs = 0.0;
            double fat = 0.0;

            if (dayStats != null) {
                totalCalories = (Integer) dayStats.getOrDefault("calories", 0);
                protein = (Double) dayStats.getOrDefault("protein", 0.0);
                carbs = (Double) dayStats.getOrDefault("carbs", 0.0);
                fat = (Double) dayStats.getOrDefault("fat", 0.0);
                status = totalCalories > user.getDailyCalorieGoal() ? "over-limit" : "under-limit";
            }

            currentWeek.add(new CalendarDay(day, date, status, totalCalories, protein, carbs, fat));
            if (currentWeek.size() == 7) {
                weeks.add(currentWeek);
                currentWeek = new ArrayList<>();
            }
        }

        if (!currentWeek.isEmpty()) {
            while (currentWeek.size() < 7) {
                currentWeek.add(null);
            }
            weeks.add(currentWeek);
        }

        return weeks;
    }

    private Map<Consumption.MealType, List<Consumption>> createMealGroups(List<Consumption> consumptions) {
        Map<Consumption.MealType, List<Consumption>> groups = new LinkedHashMap<>();
        groups.put(Consumption.MealType.BREAKFAST, new ArrayList<>());
        groups.put(Consumption.MealType.LUNCH, new ArrayList<>());
        groups.put(Consumption.MealType.DINNER, new ArrayList<>());
        groups.put(Consumption.MealType.SNACK, new ArrayList<>());

        for (Consumption consumption : consumptions) {
            groups.get(consumption.getMealType()).add(consumption);
        }
        return groups;
    }

    public static class CalendarDay {
        public int day;
        public LocalDate date;
        public String status;
        public Integer totalCalories;
        public Double protein;
        public Double carbs;
        public Double fat;

        public CalendarDay(int day, LocalDate date, String status, Integer totalCalories, Double protein, Double carbs,
                Double fat) {
            this.day = day;
            this.date = date;
            this.status = status;
            this.totalCalories = totalCalories;
            this.protein = protein;
            this.carbs = carbs;
            this.fat = fat;
        }
    }
}
