package com.calorie.tracker.service;

import com.calorie.tracker.entity.User;
import com.calorie.tracker.repository.UserRepository;
import com.calorie.tracker.service.dto.UserRegistrationDto;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
@Tag("service")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserWithEncodedPassword() {
        UserRegistrationDto dto = registrationDto("  testuser  ", "password", 2000);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.registerUser(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("testuser", captor.getValue().getUsername());
        assertEquals("encodedPassword", captor.getValue().getPassword());
        assertEquals(2000, captor.getValue().getDailyCalorieGoal());
    }

    @Test
    void shouldRejectDuplicateUsername() {
        UserRegistrationDto dto = registrationDto("existing", "password", 2000);
        when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(dto));
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void shouldFindUserByUsername() {
        User user = new User("mert", "encoded", 2200);
        when(userRepository.findByUsername("mert")).thenReturn(Optional.of(user));

        assertEquals(user, userService.findByUsername("mert"));
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> userService.findByUsername("ghost"));
    }

    @Test
    void shouldUpdateDailyGoals() {
        User user = new User("mert", "encoded", 2000);
        when(userRepository.findByUsername("mert")).thenReturn(Optional.of(user));

        userService.updateDailyGoal("mert", 2400, 160, 280, 75);

        assertEquals(2400, user.getDailyCalorieGoal());
        assertEquals(160, user.getDailyProteinGoal());
        assertEquals(280, user.getDailyCarbGoal());
        assertEquals(75, user.getDailyFatGoal());
        verify(userRepository).save(user);
    }

    @Test
    void shouldRejectInvalidDailyCalorieGoal() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateDailyGoal("mert", 0, 150, 250, 70));
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void shouldRejectInvalidMacroGoal() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.updateDailyGoal("mert", 2000, -1, 250, 70));
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    private UserRegistrationDto registrationDto(String username, String password, int calories) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setDailyCalorieGoal(calories);
        return dto;
    }
}
