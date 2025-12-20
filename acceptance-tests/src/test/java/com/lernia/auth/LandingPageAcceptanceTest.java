package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LandingPageAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void testLandingPage_Unauthenticated_ATC04() {
        // Step 1-3: Open landing page
        driver.get(baseUrl + "/");

        // ER1: Page loads successfully, no error in title
        String title = driver.getTitle().toLowerCase();
        Assertions.assertFalse(title.contains("error") || title.contains("not found"), "Landing page did not load successfully");

        // ER2: Public info and CTAs
        WebElement summary = findAny(
            By.cssSelector(".hero"),
            By.cssSelector(".summary"),
            By.xpath("//*[contains(text(),'Lernia')]"),
            By.xpath("//*[contains(text(),'study') or contains(text(),'explore')]")
        );
        Assertions.assertNotNull(summary, "Landing page summary/hero not found");

        WebElement signUp = findAny(
            By.xpath("//nav//a[contains(text(),'Sign Up') or contains(text(),'Create Account')]"),
            By.cssSelector("nav a[href*='register']"),
            By.cssSelector("header a[href*='register']")
        );
        WebElement logIn = findAny(
            By.xpath("//nav//a[contains(text(),'Log In')]"),
            By.cssSelector("nav a[href*='login']"),
            By.cssSelector("header a[href*='login']")
        );
        Assertions.assertNotNull(signUp, "Sign Up CTA not found");
        Assertions.assertNotNull(logIn, "Log In CTA not found");

        // ER3: No authenticated-only elements
        boolean hasProfile = elementExists(By.cssSelector(".profile-menu")) ||
                             elementExists(By.xpath("//*[contains(text(),'Favorites')]"));
        Assertions.assertFalse(hasProfile, "Authenticated-only elements should not be visible");

        // ER4: Auth navigation redirects to login
        signUp.click();
        wait.until(d -> d.getCurrentUrl().contains("/register"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/register"), "Sign Up CTA did not navigate to register page");
        driver.navigate().back();
        WebElement logInAgain = findAny(
            By.xpath("//nav//a[contains(text(),'Log In')]"),
            By.cssSelector("nav a[href*='login']"),
            By.cssSelector("header a[href*='login']")
        );
        Assertions.assertNotNull(logInAgain, "Log In CTA not found after navigating back");
        logInAgain.click();
        wait.until(d -> d.getCurrentUrl().contains("/login"));
        Assertions.assertTrue(driver.getCurrentUrl().contains("/login"), "Log In CTA did not navigate to login page");
    }
}