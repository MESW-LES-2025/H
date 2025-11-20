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

public class LoginAcceptanceIT extends BaseAcceptanceIT{


    @Test
    public void testLoginPageContainsFields() {
        driver.get(baseUrl + "/login");

        WebElement userField = wait.until(d -> findAny(
                By.name("username"),
                By.id("username"),
                By.name("email"),
                By.id("email"),
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[name='username']")
        ));
        WebElement passField = wait.until(d -> findAny(
                By.name("password"),
                By.id("password"),
                By.cssSelector("input[type='password']"),
                By.cssSelector("input[formcontrolname='password']")
        ));

        Assertions.assertNotNull(userField, "Username/email field not found - inspect page HTML and update selectors");
        Assertions.assertNotNull(passField, "Password field not found - inspect page HTML and update selectors");

        String title = driver.getTitle();
        Assertions.assertTrue(title.toLowerCase().contains("login"), "Page title does not contain 'Login'");
    }

    @Test
    public void testUnsuccessfulLoginShowsError() {
        driver.get(baseUrl + "/login");

        WebElement userField = wait.until(d -> findAny(
                By.name("username"),
                By.id("username"),
                By.name("email"),
                By.id("email")
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

        Assertions.assertNotNull(userField, "Login form elements not found - inspect page HTML");
        Assertions.assertNotNull(passField, "Login form elements not found - inspect page HTML");
        Assertions.assertNotNull(submit, "Login button not found - inspect page HTML");

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
                elementExists(By.cssSelector(".mat-error")) ||
                elementExists(By.xpath("//*[contains(text(),'Invalid') or contains(text(),'failed') or contains(text(),'incorrect')]"));

        Assertions.assertTrue(errorFound, "Expected error message after failed login - inspect page HTML for error element");
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

        Assertions.assertNotNull(userField);
        Assertions.assertNotNull(passField);
        Assertions.assertNotNull(submit);

        userField.clear();
        userField.sendKeys("asmith");
        passField.clear();
        passField.sendKeys("pass1");
        submit.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertFalse(currentUrl.contains("/login"), "Expected redirect after login");
        Assertions.assertTrue(currentUrl.contains("/dashboard") || currentUrl.contains("/home"), "Redirected to dashboard");

        Assertions.assertTrue(elementExists(By.id("logout-button")) || elementExists(By.xpath("//*[contains(text(),'Welcome')]")));
    }

    @Test
    public void testRememberMeCheckbox() {
        driver.get(baseUrl + "/login");
        WebElement rememberMeCheckbox = wait.until(d -> findAny(By.cssSelector("input[type='checkbox'][formcontrolname='remember']"), By.name("remember")));

        Assertions.assertNotNull(rememberMeCheckbox, "Remember me checkbox not found");
        if (!rememberMeCheckbox.isSelected()) {
            rememberMeCheckbox.click();
            Assertions.assertTrue(rememberMeCheckbox.isSelected(), "Remember me checkbox should be checked");
        }

        rememberMeCheckbox.click();
        Assertions.assertFalse(rememberMeCheckbox.isSelected(), "Remember me checkbox should be unchecked");
    }

    @Test
    public void testPasswordShowToggle() {
        driver.get(baseUrl + "/login");

        WebElement passwordField = wait.until(d -> findAny(By.id("password"), By.name("password")));
        WebElement toggleButton = findAny(By.cssSelector("button.icon[aria-label='Toggle password']"));

        Assertions.assertNotNull(passwordField, "Password field not found");
        Assertions.assertNotNull(toggleButton, "Password show toggle button not found");

        String originalType = passwordField.getAttribute("type");

        toggleButton.click();

        wait.until(ExpectedConditions.attributeToBe(passwordField, "type", originalType.equals("password") ? "text" : "password"));

        String toggledType = passwordField.getAttribute("type");
        Assertions.assertNotEquals("Password field type should toggle", originalType, toggledType);

        toggleButton.click();
        wait.until(ExpectedConditions.attributeToBe(passwordField, "type", originalType));

        Assertions.assertEquals("Password field type should toggle back", originalType, passwordField.getAttribute("type"));
    }

    @Test
    public void testForgotPasswordLink() {
        driver.get(baseUrl + "/login");

        WebElement forgotLink = findAny(By.linkText("Forgot Password"), By.cssSelector("a.link[href*='forgot']"));

        Assertions.assertNotNull(forgotLink, "Forgot Password link not found");

        forgotLink.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        Assertions.assertTrue(driver.getCurrentUrl().toLowerCase().contains("forgot"));
    }

    @Test
    public void testGuestContinueLink() {
        driver.get(baseUrl + "/login");

        WebElement guestLink = findAny(By.cssSelector("a.guest"), By.linkText("Continue as guest"));

        Assertions.assertNotNull(guestLink, "Continue as guest link not found");

        guestLink.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        Assertions.assertTrue(driver.getCurrentUrl().toLowerCase().contains("explore") || driver.getCurrentUrl().toLowerCase().contains("guest"));
    }
}
