package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class UniversitiesAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void testViewUniversityPage_ATC18() {
        // Step 1: Go to /university/1 (no login required)
        driver.get(baseUrl + "/university/1");
        wait.until(d -> d.getCurrentUrl().contains("/university/1"));

        // Step 2: Assert that the university name is displayed
        WebElement name = findAny(
            By.cssSelector(".university-name"),
            By.xpath("//h1"),
            By.xpath("//h2")
        );
        Assertions.assertNotNull(name, "University name not found on university page");

        // Step 3: Assert that the university location is displayed
        WebElement location = findAny(
            By.cssSelector("p.h4.mb-0"),
            By.cssSelector("p.stat-value.fw-bold.h5"),
            By.cssSelector("p.fw-bold.mb-0"),
            By.xpath("//p[contains(text(),'Sweden')]"),
            By.xpath("//p[contains(text(),'Stockholm')]"),
            By.xpath("//p[contains(text(),'Unknown Location')]"),
            By.xpath("//p[contains(text(),'N/A')]")
        );
        Assertions.assertNotNull(location, "University location not found on university page");

        // Step 4: Optionally, check for description or courses
        WebElement description = findAny(
            By.cssSelector("p.mb-0"),
            By.cssSelector(".description"),
            By.xpath("//p[contains(text(),'No description available')]"),
            By.xpath("//*[contains(text(),'About')]"),
            By.xpath("//*[contains(text(),'Description')]")
        );
        Assertions.assertNotNull(description, "University description not found on university page");
    }

    @Test
    public void testSearchUniversityByName_ATC19() {
        // Step 1: Go to /explore
        driver.get(baseUrl + "/explore");
        wait.until(d -> d.getCurrentUrl().contains("/explore"));

        // Step 2: Find the university search input and enter a name (e.g., "Porto")
        WebElement searchInput = wait.until(d -> findAny(
            By.cssSelector("input.hero-input"),
            By.xpath("//input[contains(@placeholder,'university')]")
        ));
        Assertions.assertNotNull(searchInput, "University search input not found");

        searchInput.clear();
        searchInput.sendKeys("Porto");

        // Step 3: Click the search button
        WebElement searchBtn = wait.until(d -> findAny(
            By.cssSelector("button.search-btn"),
            By.xpath("//button[contains(text(),'Search')]")
        ));
        Assertions.assertNotNull(searchBtn, "Search button not found");
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(searchBtn));
        searchBtn.click();

        // Step 4: Wait for the results to update (all visible cards must match or empty message)
        wait.until(d -> {
            var cards = d.findElements(By.cssSelector(".college-card"));
            if (!cards.isEmpty()) {
                for (WebElement card : cards) {
                    WebElement title = null;
                    try {
                        title = card.findElement(By.xpath(".//h3"));
                    } catch (Exception ignored) {}
                    if (title == null || !title.getText().toLowerCase().contains("porto")) {
                        return false;
                    }
                }
                return true;
            }
            // If no cards, check for empty state (no results at all)
            boolean empty = d.findElements(By.cssSelector(".college-card")).isEmpty();
            if (empty) {
                // Optionally print for debug
                System.out.println("DEBUG: No college-card found. Page source:");
                System.out.println(d.getPageSource());
                return true; // Accept as empty result
            }
            return false;
        });

        // Step 5: Assert that all visible university cards contain "Porto" in the name (case-insensitive)
        boolean found = false;
        for (WebElement card : driver.findElements(By.cssSelector(".college-card"))) {
            WebElement title = null;
            try {
                title = card.findElement(By.xpath(".//h3"));
            } catch (Exception ignored) {}
            Assertions.assertNotNull(title, "University card does not have a title element");
            Assertions.assertTrue(title.getText().toLowerCase().contains("Porto"),
                    "A university card was found that does not match the search term 'Porto': " + title.getText());
            found = true;
        }

        // If no cards, check for "No universities found" message
        if (!found) {
            WebElement emptyMsg = findAny(
                By.cssSelector(".no-more"),
                By.xpath("//*[contains(text(),'No universities found')]")
            );
            Assertions.assertNotNull(emptyMsg, "No universities found message should be displayed if no results");
        }
    }
}