package com.calorie.tracker.controller;

import com.calorie.tracker.security.SecurityConfig;
import com.calorie.tracker.service.UserService;
import com.calorie.tracker.service.dto.UserRegistrationDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@Tag("controller")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldShowLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void shouldShowRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void shouldRegisterValidUser() throws Exception {
        doNothing().when(userService).registerUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "newuser")
                        .param("password", "password")
                        .param("dailyCalorieGoal", "2000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }

    @Test
    void shouldRejectInvalidRegistrationInput() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "ab")
                        .param("password", "123")
                        .param("dailyCalorieGoal", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldShowServiceValidationError() throws Exception {
        doThrow(new IllegalArgumentException("Bu kullanıcı adı zaten kullanılıyor."))
                .when(userService).registerUser(any(UserRegistrationDto.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "existing")
                        .param("password", "password")
                        .param("dailyCalorieGoal", "2000"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attribute("error", "Bu kullanıcı adı zaten kullanılıyor."));
    }
}
