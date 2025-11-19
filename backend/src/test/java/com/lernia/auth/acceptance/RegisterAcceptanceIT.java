package com.lernia.auth.acceptance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RegisterAcceptanceIT {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

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

        FirefoxOptions options = new FirefoxOptions();
        String headless = System.getenv().getOrDefault("HEADLESS", "false");
        if (!"false".equalsIgnoreCase(headless)) {
            options.addArguments("--headless");
        }
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new FirefoxDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(4));
    }


    @Test
    public void testRegisterPageContainsFields() {
        driver.get(baseUrl + "/register");

        WebElement usernameField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='username']"),
                By.cssSelector("input[placeholder='Choose a username']"),
                By.name("username")
        ));
        WebElement emailField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='email']"),
                By.cssSelector("input[type='email']"),
                By.name("email")
        ));
        WebElement passwordField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='password']"),
                By.cssSelector("input[placeholder='Enter your password']")
        ));
        WebElement confirmPasswordField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='confirm']"),
                By.cssSelector("input[placeholder='Enter your password']")
        ));
        WebElement submitButton = findAny(
                By.cssSelector("button.primary[type='submit']"),
                By.xpath("//button[contains(text(),'Create account')]")
        );

        Assertions.assertNotNull(usernameField, "Username field not found");
        Assertions.assertNotNull(emailField, "Email field not found");
        Assertions.assertNotNull(passwordField, "Password field not found");
        Assertions.assertNotNull(confirmPasswordField, "Confirm password field not found");
        Assertions.assertNotNull(submitButton, "Create account button not found");
    }

    @Test
    public void testPasswordShowToggleOnRegister() {
        driver.get(baseUrl + "/register");

        WebElement passwordField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='password']"),
                By.cssSelector("input[placeholder='Enter your password']")
        ));
        WebElement passwordToggle = findAny(
                By.xpath("//button[@aria-label='Toggle password']")
        );

        WebElement confirmPasswordField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='confirm']"),
                By.cssSelector("input[placeholder='Enter your password']")
        ));
        WebElement confirmToggle = driver.findElements(By.xpath("//button[@aria-label='Toggle password']")).size() > 1 ?
                driver.findElements(By.xpath("//button[@aria-label='Toggle password']")).get(1) : null;

        String originalType = passwordField.getAttribute("type");
        passwordToggle.click();
        String toggledType = "password".equalsIgnoreCase(originalType) ? "text" : "password";
        wait.until(ExpectedConditions.attributeToBe(passwordField, "type", toggledType));
        Assertions.assertNotEquals(originalType, passwordField.getAttribute("type"), "Password input type should toggle");
        passwordToggle.click();
        wait.until(ExpectedConditions.attributeToBe(passwordField, "type", originalType));
        Assertions.assertEquals("Password input type should toggle back", originalType, passwordField.getAttribute("type"));

        if (confirmToggle != null) {
            String confirmOriginalType = confirmPasswordField.getAttribute("type");
            confirmToggle.click();
            String confirmToggled = "password".equalsIgnoreCase(confirmOriginalType) ? "text" : "password";
            wait.until(ExpectedConditions.attributeToBe(confirmPasswordField, "type", confirmToggled));
            Assertions.assertNotEquals("Confirm password input type should toggle", confirmOriginalType, confirmPasswordField.getAttribute("type"));
            confirmToggle.click();
            wait.until(ExpectedConditions.attributeToBe(confirmPasswordField, "type", confirmOriginalType));
            Assertions.assertEquals("Confirm password input type should toggle back", confirmOriginalType, confirmPasswordField.getAttribute("type"));
        }
    }

    @Test
    public void testUnsuccessfulRegisterShowsError() {
        driver.get(baseUrl + "/register");

        WebElement usernameField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='username']")));
        WebElement emailField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='email']")));
        WebElement passwordField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='password']")));
        WebElement confirmPasswordField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='confirm']")));
        WebElement submitButton = findAny(By.cssSelector("button.primary[type='submit']"));

        usernameField.clear();
        usernameField.sendKeys("testuser");
        emailField.clear();
        emailField.sendKeys("invalid-email");
        passwordField.clear();
        passwordField.sendKeys("pass1234");
        confirmPasswordField.clear();
        confirmPasswordField.sendKeys("pass1234");
        submitButton.click();

        wait.until(d -> elementExists(By.cssSelector(".alert-danger")) ||
                elementExists(By.cssSelector(".error")) ||
                elementExists(By.xpath("//*[contains(text(),'invalid') or contains(text(),'error') or contains(text(),'incorrect')]")));

        boolean errorFound = elementExists(By.cssSelector(".alert-danger")) ||
                elementExists(By.cssSelector(".error"));

        Assertions.assertTrue(errorFound, "Expected error message after invalid registration");
    }

    @Test
    public void testNavigateToLoginFromRegister() {
        driver.get(baseUrl + "/register");

        WebElement loginLink = findAny(
                By.linkText("Log in"),
                By.cssSelector("a.switch-link[href*='login']")
        );

        Assertions.assertNotNull(loginLink, "Log in link not found on register page");

        loginLink.click();

        wait.until(d -> d.getCurrentUrl().contains("/login"));

        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Did not navigate to login page");
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private WebElement findAny(By... selectors) {
        for (By s : selectors) {
            try {
                WebElement e = driver.findElement(s);
                if (e != null && e.isDisplayed()) return e;
            } catch (Exception ignored) {}
        }
        return null;
    }

    private boolean elementExists(By selector) {
        try {
            return !driver.findElements(selector).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

}
