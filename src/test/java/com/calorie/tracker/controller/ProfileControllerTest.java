package com.calorie.tracker.controller;

import com.calorie.tracker.entity.User;
import com.calorie.tracker.security.SecurityConfig;
import com.calorie.tracker.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(ProfileController.class)
@Import(SecurityConfig.class)
@Tag("controller")
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "mert")
    void shouldShowProfile() throws Exception {
        when(userService.findByUsername("mert")).thenReturn(new User("mert", "encoded", 2200));

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "mert")
    void shouldUpdateGoals() throws Exception {
        doNothing().when(userService).updateDailyGoal("mert", 2400, 160, 280, 75);

        mockMvc.perform(post("/profile/update-goal")
                        .with(csrf())
                        .param("dailyCalorieGoal", "2400")
                        .param("dailyProteinGoal", "160")
                        .param("dailyCarbGoal", "280")
                        .param("dailyFatGoal", "75"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attribute("success", "Hedefler güncellendi."));
    }

    @Test
    @WithMockUser(username = "mert")
    void shouldReturnValidationMessageWhenGoalUpdateFails() throws Exception {
        doThrow(new IllegalArgumentException("Günlük kalori hedefi 1-10000 arasında olmalıdır."))
                .when(userService).updateDailyGoal("mert", 0, 160, 280, 75);

        mockMvc.perform(post("/profile/update-goal")
                        .with(csrf())
                        .param("dailyCalorieGoal", "0")
                        .param("dailyProteinGoal", "160")
                        .param("dailyCarbGoal", "280")
                        .param("dailyFatGoal", "75"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("error"));
    }
}
