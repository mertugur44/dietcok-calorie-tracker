package com.calorie.tracker.ui;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("ui")
@Tag("regression")
class HtmlUnitUITest {

    @LocalServerPort
    private int port;

    @Test
    void shouldRenderLoginPageStructure() throws Exception {
        try (WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            HtmlPage page = webClient.getPage("http://localhost:" + port + "/login");
            String pageContent = page.asNormalizedText();

            assertTrue(pageContent.contains("Giriş Yap"));
            assertTrue(pageContent.contains("Kullanıcı Adı"));
        }
    }
}
