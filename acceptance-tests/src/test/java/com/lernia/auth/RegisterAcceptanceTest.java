package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class RegisterAcceptanceTest extends BaseAcceptanceTest {


    @Test
    public void testRegisterPageContainsFields() {
        System.out.println("Running testRegisterPageContainsFields");
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
        Assertions.assertEquals(originalType, passwordField.getAttribute("type"), "Password input type should toggle back");

        if (confirmToggle != null) {
            String confirmOriginalType = confirmPasswordField.getAttribute("type");
            confirmToggle.click();
            String confirmToggled = "password".equalsIgnoreCase(confirmOriginalType) ? "text" : "password";
            wait.until(ExpectedConditions.attributeToBe(confirmPasswordField, "type", confirmToggled));
            Assertions.assertNotEquals(confirmOriginalType, confirmPasswordField.getAttribute("type"), "Confirm password input type should toggle");
            confirmToggle.click();
            wait.until(ExpectedConditions.attributeToBe(confirmPasswordField, "type", confirmOriginalType));
            Assertions.assertEquals(confirmOriginalType, confirmPasswordField.getAttribute("type"), "Confirm password input type should toggle back");
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

        wait.until(d -> d.getCurrentUrl().contains("/register"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/register"), "Expected to remain on register page after invalid submission");
    }

    @Test
    public void testLandingPage_Unauthenticated_ATC04() {
        // ATC-04: View Landing Page (US04)
        driver.get(baseUrl + "/");
        WebElement summary = findAny(
            By.cssSelector(".hero"),
            By.cssSelector(".summary"),
            By.xpath("//*[contains(text(),'Lernia')]"),
            By.xpath("//*[contains(text(),'study') or contains(text(),'explore')]")
        );
        Assertions.assertNotNull(summary, "Landing page summary/hero not found");

        WebElement signUp = findAny(
            By.xpath("//a[contains(text(),'Sign Up') or contains(text(),'Create Account')]"),
            By.cssSelector("a[href*='register']")
        );
        WebElement logIn = findAny(
            By.xpath("//a[contains(text(),'Log In')]"),
            By.cssSelector("a[href*='login']")
        );
        Assertions.assertNotNull(signUp, "Sign Up CTA not found");
        Assertions.assertNotNull(logIn, "Log In CTA not found");

        boolean hasProfile = elementExists(By.cssSelector(".profile-menu")) ||
                             elementExists(By.xpath("//*[contains(text(),'Favorites')]"));
        Assertions.assertFalse(hasProfile, "Authenticated-only elements should not be visible");
    }

    @Test
    public void testRegisterWithExistingEmail_ATC06() {
        // ATC-06: Sign Up with Existing Email (US05)
        String existingEmail = "alice@example.com";
        String password = "pass12345";

        driver.get(baseUrl + "/register");
        WebElement usernameField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='username']")));
        WebElement emailField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='email']")));
        WebElement passwordField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='password']")));
        WebElement confirmPasswordField = wait.until(d -> findAny(By.cssSelector("input[formcontrolname='confirm']")));
        WebElement submitButton = findAny(By.cssSelector("button.primary[type='submit']"));

        usernameField.clear();
        usernameField.sendKeys("existinguser");
        emailField.clear();
        emailField.sendKeys(existingEmail);
        passwordField.clear();
        passwordField.sendKeys(password);
        confirmPasswordField.clear();
        confirmPasswordField.sendKeys(password);
        submitButton.click();

        wait.until(d -> d.getCurrentUrl().contains("/register"));
        boolean errorFound = waitUntilAny(errorSelectors());
        Assertions.assertTrue(errorFound, "Expected error message for existing email");
    }
}
