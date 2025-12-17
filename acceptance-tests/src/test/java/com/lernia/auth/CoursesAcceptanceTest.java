package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CoursesAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void testViewSingleCoursePage_ATC15() {
        String username = "asmith";
        String password = "pass1";

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

        // Step 2: Go directly to course/1
        driver.get(baseUrl + "/course/1");

        // Step 3: Wait for the course page to load
        wait.until(d -> d.getCurrentUrl().contains("/course/1"));

        // Step 4: Check that the course details are present
        WebElement courseTitle = findAny(
            By.cssSelector(".course-title"),
            By.xpath("//h1"),
            By.xpath("//h2")
        );
        Assertions.assertNotNull(courseTitle, "Course title not found on course page");

        WebElement courseDescription = findAny(
            By.xpath("//h3[contains(text(),'About')]/following-sibling::p[1]"),
            By.cssSelector(".text-muted"),
            By.xpath("//p[contains(@class,'text-muted')]")
        );
        Assertions.assertNotNull(courseDescription, "Course description not found on course page");
    }

    @Test
    public void testSearchCoursesByName_ATC16() {
        // Preconditions: user exists and can log in
        String username = "asmith";
        String password = "pass1";

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

        // Step 2: Go to /courses
        driver.get(baseUrl + "/courses");
        wait.until(d -> d.getCurrentUrl().contains("/courses"));

        // Step 3: Find the search input and enter a course name (e.g., "Architecture")
        WebElement searchInput = wait.until(d -> findAny(
            By.cssSelector("input.hero-input"),
            By.cssSelector("input[placeholder*='course']"),
            By.xpath("//input[contains(@placeholder,'course')]")
        ));
        Assertions.assertNotNull(searchInput, "Course search input not found");

        searchInput.clear();
        searchInput.sendKeys("Architecture");

        // Step 4: Always click the search button and wait for it to be clickable
        WebElement searchBtn = wait.until(d -> findAny(
            By.cssSelector("button.search-btn"),
            By.xpath("//button[contains(text(),'Search')]")
        ));
        Assertions.assertNotNull(searchBtn, "Search button not found");
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(searchBtn));
        searchBtn.click();

        // Step 5: Wait until all visible course cards match the search or "no results" is shown
        wait.until(d -> {
            var cards = d.findElements(By.cssSelector(".course-card"));
            if (cards.isEmpty()) {
                return !d.findElements(By.cssSelector(".empty")).isEmpty();
            }
            for (WebElement card : cards) {
                WebElement title = null;
                try {
                    title = card.findElement(By.cssSelector(".title"));
                } catch (Exception e) {
                    try {
                        title = card.findElement(By.xpath(".//h3"));
                    } catch (Exception ignored) {}
                }
                if (title == null || !title.getText().toLowerCase().contains("architecture")) {
                    return false;
                }
            }
            return true;
        });

        // Step 6: Assert that all visible course cards contain "Architecture" in the name (case-insensitive)
        boolean found = false;
        for (WebElement card : driver.findElements(By.cssSelector(".course-card"))) {
            WebElement title = null;
            try {
                title = card.findElement(By.cssSelector(".title"));
            } catch (Exception e) {
                try {
                    title = card.findElement(By.xpath(".//h3"));
                } catch (Exception ignored) {}
            }
            Assertions.assertNotNull(title, "Course card does not have a title element");
            Assertions.assertTrue(title.getText().toLowerCase().contains("architecture"),
                    "A course card was found that does not match the search term 'Architecture': " + title.getText());
            found = true;
        }

        // If no cards, check for "No courses found" message
        if (!found) {
            WebElement emptyMsg = findAny(
                By.cssSelector(".empty"),
                By.xpath("//*[contains(text(),'No courses found')]")
            );
            Assertions.assertNotNull(emptyMsg, "No courses found message should be displayed if no results");
        }
    }
}