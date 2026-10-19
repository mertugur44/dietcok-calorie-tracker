package com.calorie.tracker.e2e;

import com.calorie.tracker.entity.User;
import com.calorie.tracker.repository.UserRepository;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("e2e")
@Tag("critical")
@EnabledIfSystemProperty(named = "e2e", matches = "true")
class SeleniumE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private WebDriver driver;

    @BeforeAll
    static void setUpDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        if (userRepository.findByUsername("e2euser").isEmpty()) {
            userRepository.save(new User("e2euser", passwordEncoder.encode("password"), 2000));
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void shouldLoginAndOpenDashboard() {
        driver.get("http://localhost:" + port + "/login");
        driver.findElement(By.name("username")).sendKeys("e2euser");
        driver.findElement(By.name("password")).sendKeys("password");
        driver.findElement(By.className("auth-btn")).click();

        assertTrue(driver.getCurrentUrl().contains("/dashboard"));
        assertTrue(driver.getPageSource().contains("DietCok"));
    }
}
