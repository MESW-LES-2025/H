package com.lernia.auth.acceptance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BaseAcceptanceIT {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;
    protected int waitSeconds;

    @BeforeEach
    public void setUp() {
        String geckoDriverPath = System.getenv("GECKODRIVER_PATH");
        if (geckoDriverPath == null || geckoDriverPath.isBlank()) {
            geckoDriverPath = System.getProperty("webdriver.gecko.driver");
        }
        if (geckoDriverPath != null && !geckoDriverPath.isBlank()) {
            System.setProperty("webdriver.gecko.driver", geckoDriverPath);
        }

        baseUrl = System.getenv().getOrDefault("APP_BASE_URL", "http://localhost:4200");
        String waitSecondsStr = System.getenv().getOrDefault("WAIT_SECONDS", "10");
        try {
            waitSeconds = Integer.parseInt(waitSecondsStr);
        } catch (NumberFormatException e) {
            System.err.println("Invalid WAIT_SECONDS value: '" + waitSecondsStr + "'. Using default value 10.");
            waitSeconds = 10;
        }
        FirefoxOptions options = new FirefoxOptions();
        String headless = System.getenv().getOrDefault("HEADLESS", "false");
        if (!"false".equalsIgnoreCase(headless)) {
            options.addArguments("--headless");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new FirefoxDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(waitSeconds));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected WebElement findAny(By... selectors) {
        for (By s : selectors) {
            try {
                WebElement e = driver.findElement(s);
                if (e != null && e.isDisplayed()) return e;
            } catch (Exception ignored) {}
        }
        return null;
    }

    protected boolean elementExists(By selector) {
        try {
            return !driver.findElements(selector).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    protected By[] errorSelectors() {
        return new By[] {
                By.cssSelector(".alert-danger"),
                By.cssSelector(".error"),
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'invalid') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'error') or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'incorrect')]")
        };
    }

    protected boolean waitUntilAny(By... selectors) {
        try {
            wait.until(d -> findAny(selectors) != null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}