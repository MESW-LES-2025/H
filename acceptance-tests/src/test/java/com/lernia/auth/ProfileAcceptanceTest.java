package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProfileAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void testViewProfile_ATC11() {
        String username = "asmith";
        String password = "pass1";
        String email = "alice@example.com";

        // Step 1: Go to login page and log in
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
                By.cssSelector("input[formcontrolname='password']")
        ));
        WebElement submit = findAny(
                By.cssSelector("button[type='submit']"),
                By.cssSelector("input[type='submit']"),
                By.id("login-button"),
                By.cssSelector("button.login-btn"),
                By.xpath("//button[contains(text(),'Login') or contains(text(),'Sign In')]")
        );

        Assertions.assertNotNull(userField, "Login form elements not found");
        Assertions.assertNotNull(passField, "Login form elements not found");
        Assertions.assertNotNull(submit, "Login button not found");

        userField.clear();
        userField.sendKeys(username);
        passField.clear();
        passField.sendKeys(password);
        submit.click();

        // Step 2: Wait for redirect to profile page
        wait.until(d -> d.getCurrentUrl().contains("/profile"));

        // Step 3: Check profile page loads and displays user info
        String url = driver.getCurrentUrl();
        Assertions.assertTrue(url.contains("/profile"), "Not on profile page after login");

        WebElement nameField = wait.until(d -> findAny(
            By.cssSelector("h1.h3.fw-bold"),
            By.xpath("//h1[contains(@class,'fw-bold')]"),
            By.xpath("//h1")
        ));
        if (nameField == null) {
            System.out.println(driver.getPageSource());
        }
        Assertions.assertNotNull(nameField, "Profile name not found");

        WebElement emailField = findAny(
            By.xpath("//p[i[contains(@class,'bi-envelope')]]"),
            By.xpath("//p[contains(text(),'@')]")
        );

        Assertions.assertNotNull(emailField, "Profile email not found");

        Assertions.assertTrue(emailField.getText().contains(email), "Profile email does not match logged-in user");

        // Check for location/country
        WebElement locationField = findAny(
            By.xpath("//p[i[contains(@class,'bi-geo-alt')]]"),
            By.xpath("//p[contains(text(),'Location')]"),
            By.xpath("//p[contains(text(),'not set')]")
        );
        Assertions.assertNotNull(locationField, "Profile location/country not found");

        // Check for age badge
        WebElement ageBadge = findAny(
            By.xpath("//span[contains(@class,'badge') and contains(text(),'years')]"),
            By.xpath("//span[contains(text(),'Age')]"),
            By.xpath("//span[contains(text(),'not set')]")
        );
        Assertions.assertNotNull(ageBadge, "Profile age badge not found");

        // Check for gender badge
        WebElement genderBadge = findAny(
            By.xpath("//span[contains(@class,'badge') and .//i[contains(@class,'bi-person-fill')]]"),
            By.xpath("//span[contains(text(),'Gender')]"),
            By.xpath("//span[contains(text(),'not set')]")
        );
        Assertions.assertNotNull(genderBadge, "Profile gender badge not found");
    }

    @Test
    public void testEditProfile_ATC12() {
        // Preconditions: user exists and can log in
        String username = "asmith";
        String password = "pass1";
        String newName = "Ana Maria Silva";
        String newCountry = "Sweden";

        // Step 1: Log in
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
                By.cssSelector("input[formcontrolname='password']")
        ));
        WebElement submit = findAny(
                By.cssSelector("button[type='submit']"),
                By.cssSelector("input[type='submit']"),
                By.id("login-button"),
                By.cssSelector("button.login-btn"),
                By.xpath("//button[contains(text(),'Login') or contains(text(),'Sign In')]")
        );
        userField.clear();
        userField.sendKeys(username);
        passField.clear();
        passField.sendKeys(password);
        submit.click();

        // Step 2: Wait for profile page
        wait.until(d -> d.getCurrentUrl().contains("/profile"));

        // Step 3: Click "Edit Profile"
        WebElement editBtn = wait.until(d -> findAny(
            By.xpath("//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'edit profile')]"),
            By.cssSelector("button.btn-outline-primary"),
            By.cssSelector("button[aria-label='Edit Profile']")
        ));
        Assertions.assertNotNull(editBtn, "Edit Profile button not found");
        editBtn.click();

        // Step 4: Wait for modal and fill new name and country/location
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".modal-content")
        ));
        Assertions.assertNotNull(modal, "Edit Profile modal did not appear");

        WebElement nameInput = modal.findElement(By.cssSelector("input#name"));
        Assertions.assertNotNull(nameInput, "Name input not found in edit modal");
        nameInput.clear();
        nameInput.sendKeys(newName);

        WebElement locationInput = modal.findElement(By.cssSelector("input#location"));
        Assertions.assertNotNull(locationInput, "Location input not found in edit modal");
        locationInput.clear();
        locationInput.sendKeys(newCountry);

        // Step 5: Save changes
        WebElement saveBtn = modal.findElement(By.cssSelector("button[type='submit']"));
        Assertions.assertNotNull(saveBtn, "Save Changes button not found in edit modal");
        saveBtn.click();

        // Step 6: Wait for modal to close and profile to update
        wait.until(ExpectedConditions.invisibilityOf(modal));

        // Step 7: Assert updated name and country are shown on profile
        WebElement nameField = findAny(
            By.cssSelector("h1.h3.fw-bold"),
            By.xpath("//h1[contains(@class,'fw-bold')]"),
            By.xpath("//h1")
        );
        WebElement locationField = findAny(
            By.xpath("//p[i[contains(@class,'bi-geo-alt')]]"),
            By.xpath("//p[contains(text(),'Sweden')]"),
            By.cssSelector(".text-muted")
        );
        Assertions.assertNotNull(nameField, "Profile name not found after edit");
        Assertions.assertNotNull(locationField, "Profile location not found after edit");
        Assertions.assertTrue(nameField.getText().contains(newName), "Profile name was not updated");
        Assertions.assertTrue(locationField.getText().contains(newCountry), "Profile country/location was not updated");
    }

    // @Test
    // public void testDeleteAccount_ATC14() {
    //     String username = "deleteuser" + System.currentTimeMillis();
    //     String email = username + "@example.com";
    //     String password = "DeletePass123!";

    //     driver.get(baseUrl + "/register");
    //     WebElement usernameField = wait.until(d -> findAny(
    //             By.cssSelector("input[formcontrolname='username']"),
    //             By.name("username")
    //     ));
    //     WebElement emailField = wait.until(d -> findAny(
    //             By.cssSelector("input[formcontrolname='email']"),
    //             By.name("email")
    //     ));
    //     WebElement passwordField = wait.until(d -> findAny(
    //             By.cssSelector("input[formcontrolname='password']")
    //     ));
    //     WebElement confirmPasswordField = wait.until(d -> findAny(
    //             By.cssSelector("input[formcontrolname='confirm']")
    //     ));
    //     WebElement submitButton = findAny(
    //             By.cssSelector("button.primary[type='submit']"),
    //             By.xpath("//button[contains(text(),'Create account')]"),
    //             By.xpath("//button[contains(text(),'Register')]")
    //     );

    //     usernameField.clear();
    //     usernameField.sendKeys(username);
    //     emailField.clear();
    //     emailField.sendKeys(email);
    //     passwordField.clear();
    //     passwordField.sendKeys(password);
    //     confirmPasswordField.clear();
    //     confirmPasswordField.sendKeys(password);
    //     submitButton.click();

    //     wait.until(d -> d.getCurrentUrl().contains("/login"));
    //     driver.get(baseUrl + "/login");

    //     WebElement loginField = wait.until(d -> findAny(
    //             By.name("username"),
    //             By.id("username"),
    //             By.name("email"),
    //             By.id("email"),
    //             By.cssSelector("input[type='email']"),
    //             By.cssSelector("input[name='username']")
    //     ));
    //     WebElement loginPasswordField = wait.until(d -> findAny(
    //             By.name("password"),
    //             By.id("password"),
    //             By.cssSelector("input[formcontrolname='password']")
    //     ));
    //     WebElement loginButton = findAny(
    //             By.cssSelector("button[type='submit']"),
    //             By.cssSelector("button.primary[type='submit']"),
    //             By.xpath("//button[contains(text(),'Login')]")
    //     );

    //     loginField.clear();
    //     loginField.sendKeys(username);
    //     loginPasswordField.clear();
    //     loginPasswordField.sendKeys(password);
    //     loginButton.click();

    //     wait.until(d -> d.getCurrentUrl().contains("/profile"));

    //     // Click "Delete Account"
    //     WebElement deleteBtn = findAny(
    //         By.xpath("//button[contains(text(),'Delete Account')]"),
    //         By.cssSelector("button.btn-outline-danger"),
    //         By.cssSelector("button[aria-label='Delete Account']")
    //     );
    //     Assertions.assertNotNull(deleteBtn, "Delete Account button not found");
    //     deleteBtn.click();

    //     // Confirm deletion in modal/dialog
    //     WebElement confirmBtn = wait.until(d -> findAny(
    //         By.xpath("//button[contains(text(),'Yes, delete')]"),
    //         By.xpath("//button[contains(text(),'Confirm')]"),
    //         By.cssSelector("button.btn-danger"),
    //         By.cssSelector("button.confirm-delete")
    //     ));
    //     Assertions.assertNotNull(confirmBtn, "Confirm delete button not found");
    //     confirmBtn.click();
    //     System.out.println("DEBUG: Clicked confirm delete.");
    //     try {
    //         Thread.sleep(2000); 
    //     } catch (InterruptedException e) {
    //         throw new RuntimeException(e);
    //     }

    //     System.out.println("DEBUG: Current URL after confirm: " + driver.getCurrentUrl());
    //     System.out.println("DEBUG: Page source after confirm:\n" + driver.getPageSource());

    //     // Wait for redirect to landing or confirmation
    //     wait.until(d -> {
    //         String url = d.getCurrentUrl();
    //         return url.endsWith("/") || url.contains("/home") || url.contains("/login");
    //     });

    //     // Check for confirmation message
    //     boolean confirmationShown = waitUntilAny(
    //         By.cssSelector(".alert-success"),
    //         By.xpath("//*[contains(text(),'account has been deleted')]"),
    //         By.xpath("//*[contains(text(),'deleted successfully')]")
    //     );
    //     Assertions.assertTrue(confirmationShown, "No confirmation message shown after account deletion");

    //     // Try to log in again (should fail)
    //     driver.get(baseUrl + "/login");
    //     WebElement userField = wait.until(d -> findAny(
    //             By.name("username"),
    //             By.id("username"),
    //             By.name("email"),
    //             By.id("email"),
    //             By.cssSelector("input[type='email']"),
    //             By.cssSelector("input[name='username']")
    //     ));
    //     WebElement passField = wait.until(d -> findAny(
    //             By.name("password"),
    //             By.id("password"),
    //             By.cssSelector("input[formcontrolname='password']")
    //     ));
    //     WebElement submit = findAny(
    //             By.cssSelector("button[type='submit']"),
    //             By.cssSelector("button.primary[type='submit']"),
    //             By.xpath("//button[contains(text(),'Login')]")
    //     );

    //     userField.clear();
    //     userField.sendKeys(username);
    //     passField.clear();
    //     passField.sendKeys(password);
    //     submit.click();

    //     // Wait for error message
    //     boolean errorFound = waitUntilAny(
    //         By.cssSelector(".alert-danger"),
    //         By.cssSelector(".error"),
    //         By.xpath("//*[contains(text(),'Invalid') or contains(text(),'deleted') or contains(text(),'not found')]")
    //     );
    //     Assertions.assertTrue(errorFound, "Expected login failure after account deletion");
    // }


    @Test
    public void testViewFavoritesInProfile_ATC29() {
        // Scenario: View Favorites in Profile (US20)
        loginAsTestUser();

        driver.get(baseUrl + "/profile/1");
        wait.until(d -> d.getCurrentUrl().contains("/profile/1"));

        // Check for favorite universities
        WebElement universitiesTab = wait.until(d -> d.findElement(
            By.xpath("//button[contains(.,'Universities') and contains(@class,'active')]")
        ));
        Assertions.assertNotNull(universitiesTab, "Universities tab not active");

        WebElement favoriteUniversity = findAny(
            By.cssSelector(".card.h-100"),
            By.xpath("//div[contains(@class,'card') and contains(@class,'h-100')]")
        );
        Assertions.assertNotNull(favoriteUniversity, "No favorite universities found");

        // check for favorite courses
        WebElement coursesTab = wait.until(d -> d.findElement(
            By.xpath("//button[contains(.,'Courses')]")
        ));
        coursesTab.click();

        WebElement favoriteCourse = findAny(
            By.cssSelector(".course-card"),
            By.xpath("//article[contains(@class,'course-card')]")
        );
        Assertions.assertNotNull(favoriteCourse, "No favorite courses found");
    }
}