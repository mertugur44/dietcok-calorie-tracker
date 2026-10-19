package com.calorie.tracker.service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz.")
    @Size(min = 3, max = 50, message = "Kullanıcı adı 3-50 karakter arasında olmalıdır.")
    private String username;

    @NotBlank(message = "Şifre boş bırakılamaz.")
    @Size(min = 6, max = 72, message = "Şifre en az 6 karakter olmalıdır.")
    private String password;

    @NotNull(message = "Günlük kalori hedefi gereklidir.")
    @Min(value = 1, message = "Günlük kalori hedefi 0'dan büyük olmalıdır.")
    @Max(value = 10000, message = "Günlük kalori hedefi 10000'i aşamaz.")
    private Integer dailyCalorieGoal;
}
