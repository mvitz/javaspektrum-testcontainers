package de.mvitz.js.tc;

import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.BrowserWebDriverContainer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

public class SeleniumTest {

    @Rule
    public BrowserWebDriverContainer<?> chrome =
        new BrowserWebDriverContainer<>()
            .withCapabilities(chromeWithoutCookies())
            .withRecordingMode(RECORD_ALL, new File("."));

    @Test
    public void searchGoogle() {
        WebDriver driver = chrome.getWebDriver();

        driver.get("https://www.google.de");

        WebElement searchInputField = driver.findElement(By.name("q"));
        searchInputField.sendKeys("Testcontainers");
        searchInputField.submit();

        new WebDriverWait(driver, 10).until(
            webDriver -> webDriver.getTitle()
                .toLowerCase().startsWith("testcontainers")
        );

        assertEquals("Testcontainers - Google Suche", driver.getTitle());
        driver.quit();
    }

    private static Capabilities chromeWithoutCookies() {
        Map<String, Object> contentSettings = new HashMap<>();
        contentSettings.put("cookies", 2);

        Map<String, Object> profile = new HashMap<>();
        profile.put("managed_default_content_settings", contentSettings);

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile", profile);

        return new ChromeOptions()
            .setExperimentalOption("prefs", prefs);
    }
}
