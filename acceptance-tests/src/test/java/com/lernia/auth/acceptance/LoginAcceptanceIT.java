package com.lernia.auth.acceptance;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginAcceptanceIT extends BaseAcceptanceIT {


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
                By.id("username")
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
        Assertions.assertTrue(currentUrl.contains("/profile"));
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
        Assertions.assertNotEquals(originalType, toggledType, "Password field type should toggle");

        toggleButton.click();
        wait.until(ExpectedConditions.attributeToBe(passwordField, "type", originalType));

        Assertions.assertEquals(originalType, passwordField.getAttribute("type"), "Password field type should toggle back");
    }

    /*@Test
    public void testForgotPasswordLink() {
        driver.get(baseUrl + "/login");

        WebElement forgotLink = findAny(By.linkText("Forgot Password"), By.cssSelector("a.link[href*='forgot']"));

        Assertions.assertNotNull(forgotLink, "Forgot Password link not found");

        forgotLink.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        Assertions.assertTrue(driver.getCurrentUrl().toLowerCase().contains("forgot"));
    }*/

    @Test
    public void testGuestContinueLink() {
        driver.get(baseUrl + "/login");

        WebElement guestLink = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("a.guest")));

        Assertions.assertNotNull(guestLink, "Continue as guest link not found");

        guestLink.click();

        wait.until(d -> !d.getCurrentUrl().contains("/login"));

        String currentUrl = driver.getCurrentUrl().toLowerCase();
        Assertions.assertTrue(currentUrl.contains("explore"),
                "URL does not contain 'explore'. Current URL: " + currentUrl);
    }

    @Test
    public void testLogoutAfterRegisterAndLogin() {
        String unique = "accuser" + System.currentTimeMillis();
        String username = unique;
        String email = unique + "@example.com";
        String password = "Passw0rd!";

        // Register new user
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
                By.xpath("//button[contains(text(),'Create account')]"),
                By.xpath("//button[contains(text(),'Register')]")
        );

        usernameField.clear();
        usernameField.sendKeys(username);
        emailField.clear();
        emailField.sendKeys(email);
        passwordField.clear();
        passwordField.sendKeys(password);
        confirmPasswordField.clear();
        confirmPasswordField.sendKeys(password);
        submitButton.click();

        try {
            wait.until(d -> d.getCurrentUrl().contains("/login"));
        } catch (Exception e) {
        }

        if (!driver.getCurrentUrl().contains("/login")) {
            driver.get(baseUrl + "/login");
        }

        WebElement loginTextField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='text']"),
                By.cssSelector("input[placeholder='Email or username']"),
                By.name("text"),
                By.name("username"),
                By.cssSelector("input[type='email']"),
                By.cssSelector("input[formcontrolname='username']")
        ));
        WebElement loginPasswordField = wait.until(d -> findAny(
                By.cssSelector("input[formcontrolname='password']"),
                By.cssSelector("input[placeholder='Enter your password']"),
                By.cssSelector("input[type='password']")
        ));
        WebElement loginButton = findAny(
                By.cssSelector("button.primary[type='submit']"),
                By.xpath("//button[contains(text(),'Log in')]"),
                By.xpath("//button[contains(text(),'Login')]")
        );

        loginTextField.clear();
        loginTextField.sendKeys(username);
        loginPasswordField.clear();
        loginPasswordField.sendKeys(password);
        loginButton.click();

        wait.until(d -> d.getCurrentUrl().contains("/profile"));

        WebElement logoutBtn = wait.until(d -> findAny(
                By.cssSelector("button.btn-logout"),
                By.xpath("//button[contains(text(),'Log Out')]"),
                By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),'log out')]")
        ));
        Assertions.assertNotNull(logoutBtn, "Logout button should be visible after login");

        logoutBtn.click();

        // Wait for redirect to Home
        wait.until(d -> {
            String url = d.getCurrentUrl();
            return url.endsWith("/") || url.endsWith("/home");
        });

        WebElement profileBtn = wait.until(d -> findAny(
                By.cssSelector("button[aria-label='Login']"),
                By.cssSelector("button[routerLink='/login']"),
                By.xpath("//button[.//img[contains(@src, 'profile')]]")
        ));
        Assertions.assertNotNull(profileBtn, "Profile/Login icon should be visible after logout");
        
        profileBtn.click();

        wait.until(d -> d.getCurrentUrl().contains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Clicking profile button after logout should redirect to login page");
    }
}
