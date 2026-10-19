package com.calorie.tracker.controller;

import com.calorie.tracker.entity.Consumption;
import com.calorie.tracker.security.SecurityConfig;
import com.calorie.tracker.service.ConsumptionService;
import com.calorie.tracker.service.FoodService;
import com.calorie.tracker.service.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
@Tag("controller")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsumptionService consumptionService;

    @MockBean
    private FoodService foodService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "mert")
    void shouldAddConsumption() throws Exception {
        LocalDate date = LocalDate.now();
        doNothing().when(consumptionService).addConsumption(
                "mert", 1L, date, Consumption.MealType.BREAKFAST, 1.0);

        mockMvc.perform(post("/consumption/add")
                        .with(csrf())
                        .param("date", date.toString())
                        .param("foodId", "1")
                        .param("mealType", "BREAKFAST")
                        .param("amount", "1.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/day/" + date))
                .andExpect(flash().attribute("success", "Besin kaydı eklendi."));
    }

    @Test
    @WithMockUser(username = "mert")
    void shouldReturnValidationMessageWhenConsumptionCannotBeAdded() throws Exception {
        LocalDate date = LocalDate.now();
        doThrow(new IllegalArgumentException("Miktar 0'dan büyük olmalıdır."))
                .when(consumptionService).addConsumption(
                        eq("mert"), eq(1L), eq(date), eq(Consumption.MealType.BREAKFAST), any(Double.class));

        mockMvc.perform(post("/consumption/add")
                        .with(csrf())
                        .param("date", date.toString())
                        .param("foodId", "1")
                        .param("mealType", "BREAKFAST")
                        .param("amount", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "mert")
    void shouldDeleteConsumptionWithPostRequest() throws Exception {
        LocalDate date = LocalDate.now();
        doNothing().when(consumptionService).deleteConsumption("mert", 10L);

        mockMvc.perform(post("/consumption/10/delete")
                        .with(csrf())
                        .param("date", date.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/day/" + date))
                .andExpect(flash().attribute("success", "Besin kaydı silindi."));
    }
}
