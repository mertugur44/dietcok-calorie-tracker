package com.calorie.tracker.service;

import com.calorie.tracker.entity.User;
import com.calorie.tracker.repository.UserRepository;
import com.calorie.tracker.service.dto.UserRegistrationDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final int MAX_DAILY_CALORIES = 10_000;
    private static final int MAX_DAILY_MACRO_GRAMS = 1_000;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(UserRegistrationDto registrationDto) {
        String username = registrationDto.getUsername().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten kullanılıyor.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setDailyCalorieGoal(registrationDto.getDailyCalorieGoal());

        userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Kullanıcı bulunamadı."));
    }

    @Transactional
    public void updateDailyGoal(String username, int dailyCalorieGoal, Integer proteinGoal, Integer carbGoal,
            Integer fatGoal) {
        validateDailyCalorieGoal(dailyCalorieGoal);
        validateMacroGoal(proteinGoal, "Protein");
        validateMacroGoal(carbGoal, "Karbonhidrat");
        validateMacroGoal(fatGoal, "Yağ");

        User user = findByUsername(username);
        user.setDailyCalorieGoal(dailyCalorieGoal);
        user.setDailyProteinGoal(proteinGoal);
        user.setDailyCarbGoal(carbGoal);
        user.setDailyFatGoal(fatGoal);
        userRepository.save(user);
    }

    private void validateDailyCalorieGoal(int dailyCalorieGoal) {
        if (dailyCalorieGoal <= 0 || dailyCalorieGoal > MAX_DAILY_CALORIES) {
            throw new IllegalArgumentException("Günlük kalori hedefi 1-10000 arasında olmalıdır.");
        }
    }

    private void validateMacroGoal(Integer value, String label) {
        if (value != null && (value < 0 || value > MAX_DAILY_MACRO_GRAMS)) {
            throw new IllegalArgumentException(label + " hedefi 0-1000 gram arasında olmalıdır.");
        }
    }
}
