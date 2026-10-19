package com.calorie.tracker.integration;

import com.calorie.tracker.entity.User;
import com.calorie.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("integration")
class DashboardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        if (userRepository.findByUsername("integrationUser").isEmpty()) {
            User user = new User();
            user.setUsername("integrationUser");
            user.setPassword(passwordEncoder.encode("password"));
            user.setDailyCalorieGoal(2000);
            user.setDailyProteinGoal(150);
            user.setDailyCarbGoal(200);
            user.setDailyFatGoal(70);
            userRepository.save(user);
        }
    }

    @Test
    void shouldRenderDashboardForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/dashboard").with(user("integrationUser")))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("user", "weeks", "currentMonthName", "currentYear"));
    }

    @Test
    void shouldRedirectUnauthenticatedUser() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
