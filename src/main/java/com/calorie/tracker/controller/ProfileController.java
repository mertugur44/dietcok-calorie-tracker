package com.calorie.tracker.controller;

import com.calorie.tracker.entity.User;
import com.calorie.tracker.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile/update-goal")
    public String updateGoal(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("dailyCalorieGoal") int dailyCalorieGoal,
            @RequestParam(value = "dailyProteinGoal", required = false) Integer dailyProteinGoal,
            @RequestParam(value = "dailyCarbGoal", required = false) Integer dailyCarbGoal,
            @RequestParam(value = "dailyFatGoal", required = false) Integer dailyFatGoal,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateDailyGoal(userDetails.getUsername(), dailyCalorieGoal, dailyProteinGoal, dailyCarbGoal,
                    dailyFatGoal);
            redirectAttributes.addFlashAttribute("success", "Hedefler güncellendi.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profile";
    }
}
