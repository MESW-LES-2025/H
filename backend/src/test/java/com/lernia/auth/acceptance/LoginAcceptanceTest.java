package com.lernia.auth.acceptance;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginAcceptanceTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;



    @Before
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testLoginPageContainsFields() {
        driver.get(baseUrl + "/login");



        WebElement userField = wait.until(d -> findAny(
                By.name("username"),
                By.id("username"),
                By.name("email"),
                By.id("email"),
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name='username']"),
                By.cssSelector("input[formcontrolname='username']"),
                By.cssSelector("input[formcontrolname='email']")
        ));
        WebElement passField = wait.until(d -> findAny(
                By.name("password"),
                By.id("password"),
                By.cssSelector("input[type='password']"),
                By.cssSelector("input[formcontrolname='password']")
        ));

        assertNotNull("Username/email field not found - inspect page HTML and update selectors", userField);
        assertNotNull("Password field not found - inspect page HTML and update selectors", passField);

        String title = driver.getTitle();
        assertTrue("Page title does not contain 'Login'", title != null && title.toLowerCase().contains("login"));
    }

    @Test
    public void testUnsuccessfulLoginShowsError() {
        driver.get(baseUrl + "/login");

        WebElement userField = wait.until(d -> findAny(
                By.name("username"),
                By.id("username"),
                By.name("email"),
                By.id("email"),
                By.cssSelector("input[formcontrolname='username']"),
                By.cssSelector("input[formcontrolname='email']")
        ));
        WebElement passField = wait.until(d -> findAny(
                By.name("password"),
                By.id("password"),
                By.cssSelector("input[formcontrolname='password']")
        ));
        WebElement submit = findAny(
                By.cssSelector("button[type='submit']"),
                By.cssSelector("input[type='submit']"),
                By.id("login-button"),
                By.cssSelector("button.login-btn"),
                By.xpath("//button[contains(text(),'Login') or contains(text(),'Sign In')]")
        );

        assertNotNull("Login form elements not found - inspect page HTML", userField);
        assertNotNull("Login form elements not found - inspect page HTML", passField);
        assertNotNull("Login button not found - inspect page HTML", submit);

        userField.clear();
        userField.sendKeys("invalid_user");
        passField.clear();
        passField.sendKeys("wrongpassword");
        submit.click();

        wait.until(d -> elementExists(By.className("error")) ||
                elementExists(By.cssSelector(".alert-danger")) ||
                elementExists(By.id("login-error")) ||
                elementExists(By.cssSelector(".toast-error")) ||
                elementExists(By.cssSelector(".mat-error")) ||
                elementExists(By.xpath("//*[contains(text(),'Invalid') or contains(text(),'failed') or contains(text(),'incorrect')]")));

        boolean errorFound = elementExists(By.className("error")) ||
                elementExists(By.cssSelector(".alert-danger")) ||
                elementExists(By.id("login-error")) ||
                elementExists(By.cssSelector(".toast-error")) ||
                elementExists(By.cssSelector(".mat-error"));

        assertTrue("Expected error message after failed login - inspect page HTML for error element", errorFound);
    }

    @Test
    public void testSuccessfulLoginRedirects() {
        driver.get(baseUrl + "/login");

        WebElement userField = wait.until(d -> findAny(
                By.name("username"),
                By.id("username"),
                By.name("email"),
                By.id("email")
        ));
        WebElement passField = wait.until(d -> findAny(
                By.name("password"),
                By.id("password")
        ));
        WebElement submit = findAny(
                By.cssSelector("button[type='submit']"),
                By.id("login-button")
        );

        assertNotNull(userField);
        assertNotNull(passField);
        assertNotNull(submit);

        userField.clear();
        userField.sendKeys("asmith");
        passField.clear();
        passField.sendKeys("pass1");
        submit.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        String currentUrl = driver.getCurrentUrl();
        assertFalse("Expected redirect after login", currentUrl.contains("/login"));
        assertTrue("Redirected to dashboard", currentUrl.contains("/dashboard") || currentUrl.contains("/home"));

        assertTrue(elementExists(By.id("logout-button")) || elementExists(By.xpath("//*[contains(text(),'Welcome')]")));
    }

    @Test
    public void testRememberMeCheckbox() {
        driver.get(baseUrl + "/login");
        WebElement rememberMeCheckbox = wait.until(d -> findAny(By.cssSelector("input[type='checkbox'][formcontrolname='remember']"), By.name("remember")));

        assertNotNull("Remember me checkbox not found", rememberMeCheckbox);

        if (!rememberMeCheckbox.isSelected()) {
            rememberMeCheckbox.click();
            assertTrue("Remember me checkbox should be checked", rememberMeCheckbox.isSelected());
        }

        rememberMeCheckbox.click();
        assertFalse("Remember me checkbox should be unchecked", rememberMeCheckbox.isSelected());
    }

    @Test
    public void testPasswordShowToggle() {
        driver.get(baseUrl + "/login");

        WebElement passwordField = wait.until(d -> findAny(By.id("password"), By.name("password")));
        WebElement toggleButton = findAny(By.cssSelector("button.icon[aria-label='Toggle password']"));

        assertNotNull("Password field not found", passwordField);
        assertNotNull("Password show toggle button not found", toggleButton);

        String originalType = passwordField.getAttribute("type");

        toggleButton.click();

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        String toggledType = passwordField.getAttribute("type");

        assertNotEquals("Password field type should toggle", originalType, toggledType);

        toggleButton.click();
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        assertEquals("Password field type should toggle back", originalType, passwordField.getAttribute("type"));
    }

    @Test
    public void testForgotPasswordLink() {
        driver.get(baseUrl + "/login");

        WebElement forgotLink = findAny(By.linkText("Forgot Password"), By.cssSelector("a.link[href*='forgot']"));

        assertNotNull("Forgot Password link not found", forgotLink);

        forgotLink.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        assertTrue(driver.getCurrentUrl().toLowerCase().contains("forgot"));
    }

    @Test
    public void testGuestContinueLink() {
        driver.get(baseUrl + "/login");

        WebElement guestLink = findAny(By.cssSelector("a.guest"), By.linkText("Continue as guest"));

        assertNotNull("Continue as guest link not found", guestLink);

        guestLink.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        assertTrue(driver.getCurrentUrl().toLowerCase().contains("explore") || driver.getCurrentUrl().toLowerCase().contains("guest"));
    }


    @After
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
