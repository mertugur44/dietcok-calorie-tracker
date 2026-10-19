package com.calorie.tracker.config;

import com.calorie.tracker.entity.Food;
import com.calorie.tracker.repository.FoodRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class FoodDataSeeder implements CommandLineRunner {

    private final FoodRepository foodRepository;

    public FoodDataSeeder(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    @Override
    public void run(String... args) {
        if (foodRepository.count() > 0) {
            return;
        }

        addFood("Haşlanmış Yumurta", 155, 12.6, 1.1, 10.6);
        addFood("Omlet (Sade)", 154, 11.0, 0.6, 12.0);
        addFood("Menemen", 120, 5.0, 6.0, 9.0);
        addFood("Beyaz Peynir (Tam Yağlı)", 310, 20.0, 2.0, 25.0);
        addFood("Kaşar Peyniri", 350, 27.0, 1.0, 26.0);
        addFood("Siyah Zeytin", 350, 1.0, 6.0, 35.0);
        addFood("Bal", 304, 0.3, 82.0, 0.0);
        addFood("Tereyağı", 717, 0.9, 0.1, 81.0);
        addFood("Simit", 270, 10.0, 50.0, 5.0);
        addFood("Poğaça (Peynirli)", 300, 8.0, 35.0, 15.0);
        addFood("Tam Buğday Ekmeği", 250, 13.0, 41.0, 4.0);
        addFood("Yulaf Ezmesi", 370, 14.0, 56.0, 7.0);

        addFood("Mercimek Çorbası", 56, 3.0, 8.0, 1.5);
        addFood("Tarhana Çorbası", 60, 2.5, 9.0, 1.5);
        addFood("Ezogelin Çorbası", 65, 3.5, 9.0, 2.0);
        addFood("Yayla Çorbası", 48, 1.5, 5.0, 2.5);
        addFood("Domates Çorbası", 35, 1.0, 5.0, 1.5);

        addFood("Tavuk Göğsü (Haşlanmış)", 165, 31.0, 0.0, 3.6);
        addFood("Tavuk Izgara", 200, 28.0, 0.0, 9.0);
        addFood("Köfte (Izgara)", 250, 18.0, 5.0, 18.0);
        addFood("Kuru Fasulye (Etli)", 140, 9.0, 12.0, 6.0);
        addFood("Nohut Yemeği", 160, 8.0, 15.0, 7.0);
        addFood("Taze Fasulye", 40, 2.0, 4.0, 2.0);
        addFood("Karnıyarık", 180, 8.0, 8.0, 12.0);
        addFood("İskender Kebap", 220, 15.0, 15.0, 10.0);
        addFood("Döner (Et)", 250, 20.0, 2.0, 18.0);
        addFood("Lahmacun", 220, 10.0, 25.0, 8.0);
        addFood("Pide (Kıymalı)", 250, 12.0, 35.0, 8.0);
        addFood("Hamsi Tava", 280, 18.0, 5.0, 20.0);
        addFood("Somon Izgara", 208, 20.0, 0.0, 13.0);

        addFood("Pirinç Pilavı", 130, 2.7, 28.0, 0.3);
        addFood("Bulgur Pilavı", 110, 3.5, 23.0, 0.5);
        addFood("Makarna (Sade)", 130, 5.0, 25.0, 0.5);
        addFood("Patates Kızartması", 312, 3.4, 41.0, 15.0);
        addFood("Patates Haşlama", 87, 1.9, 20.0, 0.1);

        addFood("Çoban Salatası", 40, 1.0, 4.0, 2.0);
        addFood("Sezar Salata", 150, 8.0, 10.0, 10.0);
        addFood("Yoğurt (Tam Yağlı)", 61, 3.5, 4.7, 3.3);

        addFood("Baklava", 400, 6.0, 50.0, 20.0);
        addFood("Sütlaç", 110, 3.0, 20.0, 2.0);
        addFood("Kazandibi", 130, 3.0, 25.0, 3.0);
        addFood("Çiğ Köfte", 200, 10.0, 30.0, 5.0);
        addFood("Çikolata (Sütlü)", 535, 7.7, 59.0, 29.0);
        addFood("Muz", 89, 1.1, 22.8, 0.3);
        addFood("Elma", 52, 0.3, 13.8, 0.2);
        addFood("Portakal", 47, 0.9, 11.8, 0.1);
        addFood("Ceviz", 654, 15.0, 14.0, 65.0);
        addFood("Fındık", 628, 15.0, 17.0, 61.0);
        addFood("Badem", 579, 21.0, 22.0, 50.0);
    }

    private void addFood(String name, int calories, double protein, double carbs, double fat) {
        Food food = new Food(null, name, calories, protein, carbs, fat);
        foodRepository.save(food);
    }
}
