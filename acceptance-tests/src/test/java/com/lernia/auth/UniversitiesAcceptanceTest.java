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

        // Step 2: Find the university search input and enter a name
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
            boolean empty = d.findElements(By.cssSelector(".college-card")).isEmpty();
            if (empty) {
                System.out.println("DEBUG: No college-card found. Page source:");
                System.out.println(d.getPageSource());
                return true; 
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
            Assertions.assertTrue(title.getText().toLowerCase().contains("porto"),
                    "A university card was found that does not match the search term 'porto': " + title.getText());
            found = true;
        }

        if (!found) {
            WebElement emptyMsg = findAny(
                By.cssSelector(".no-more"),
                By.xpath("//*[contains(text(),'No universities found')]")
            );
            Assertions.assertNotNull(emptyMsg, "No universities found message should be displayed if no results");
        }
    }

    @Test
    public void testFilterUniversities_ATC20() {
        // Step 1: Go to /explore 
        driver.get(baseUrl + "/explore");
        wait.until(d -> d.getCurrentUrl().contains("/explore"));

        // Step 2: Set filters: Country = Spain, Scholarship = Yes
        WebElement countrySelect = wait.until(d -> findAny(
            By.cssSelector("select.filter-select"),
            By.xpath("//select[contains(@class,'filter-select')]")
        ));
        Assertions.assertNotNull(countrySelect, "Country select not found");
        countrySelect.click();
        // Select Spain
        for (WebElement option : countrySelect.findElements(By.tagName("option"))) {
            if (option.getText().trim().equalsIgnoreCase("Spain")) {
                option.click();
                break;
            }
        }

        WebElement scholarshipSelect = wait.until(d -> findAny(
            By.xpath("//label[.//span[contains(text(),'Scholarships')]]//select"),
            By.cssSelector("select.filter-select")
        ));
        Assertions.assertNotNull(scholarshipSelect, "Scholarship select not found");
        scholarshipSelect.click();
        for (WebElement option : scholarshipSelect.findElements(By.tagName("option"))) {
            if (option.getText().trim().equalsIgnoreCase("Yes")) {
                option.click();
                break;
            }
        }

        // Step 3: Click the Search button
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
                    // Check country
                    boolean countryOk = false;
                    try {
                        String meta = card.findElement(By.cssSelector(".meta")).getText().toLowerCase();
                        countryOk = meta.contains("spain");
                    } catch (Exception ignored) {}
                    // Check scholarship (blurb or meta may mention scholarship)
                    boolean scholarshipOk = false;
                    try {
                        String blurb = card.findElement(By.xpath(".//p")).getText().toLowerCase();
                        scholarshipOk = blurb.contains("scholarship") || blurb.contains("grant");
                    } catch (Exception ignored) {}
                    if (!(countryOk && scholarshipOk)) {
                        return false;
                    }
                }
                return true;
            }
            // If no cards, check for empty state
            boolean empty = d.findElements(By.cssSelector(".college-card")).isEmpty();
            if (empty) {
                return true;
            }
            return false;
        });

        // Step 5: Assert that all visible university cards match the filters
        boolean found = false;
        for (WebElement card : driver.findElements(By.cssSelector(".college-card"))) {
        // Check country
        boolean countryOk = false;
        try {
            String country = card.findElement(By.cssSelector(".badge.country")).getText().toLowerCase();
            countryOk = country.contains("spain");
        } catch (Exception ignored) {}
        // Check scholarship
        boolean scholarshipOk = false;
        try {
            String scholarship = card.findElement(By.cssSelector(".badge.scholarship")).getText().toLowerCase();
            scholarshipOk = scholarship.contains("scholarship");
        } catch (Exception ignored) {}
            Assertions.assertTrue(countryOk, "University card does not have country Spain");
            Assertions.assertTrue(scholarshipOk, "University card does not mention scholarship/grant");
            found = true;
        }

        // If no cards, check for "No universities found" message
        if (!found) {
            WebElement emptyMsg = findAny(
                By.cssSelector(".no-more"),
                By.xpath("//*[contains(text(),'No universities found')]"),
                By.xpath("//*[contains(text(),'No universities match your criteria')]")
            );
            Assertions.assertNotNull(emptyMsg, "No universities found message should be displayed if no results");
        }
    }


    @Test
    public void testViewUniversityDetails_ATC25() {
        // Scenario: View University Details (US17)
        driver.get(baseUrl + "/university/1");
        wait.until(d -> d.getCurrentUrl().contains("/university/1"));

        WebElement name = findAny(By.cssSelector(".university-name"), By.xpath("//h1"), By.xpath("//h2"));
        Assertions.assertNotNull(name, "University name not found");

        WebElement location = findAny(By.cssSelector(".university-location"), By.xpath("//*[contains(text(),'City')]"), By.xpath("//*[contains(text(),'Country')]"));
        Assertions.assertNotNull(location, "University location not found");

        WebElement programs = findAny(By.cssSelector(".university-programs"), By.xpath("//*[contains(text(),'Programs')]"));
        Assertions.assertNotNull(programs, "University programs not found");
    }

    @Test
    public void testViewReviewsOnUniversity_ATC27() {
        // Scenario: View Reviews on Universities (US19)
        driver.get(baseUrl + "/university/1");
        wait.until(d -> d.getCurrentUrl().contains("/university/1"));

        WebElement reviewsSection = findAny(By.cssSelector(".reviews-section"), By.xpath("//*[contains(text(),'Reviews')]"));
        Assertions.assertNotNull(reviewsSection, "Reviews section not found");

        // Check at least one review is present
        WebElement review = findAny(By.cssSelector(".review"), By.xpath("//*[contains(@class,'review')]"));
        Assertions.assertNotNull(review, "No reviews found for university");
    }

    @Test
    public void testAddReviewToUniversity_ATC28() {
        // Scenario: Add Review to a University (US19)
        loginAsTestUser(); // Implement this helper to log in

        driver.get(baseUrl + "/university/1");
        wait.until(d -> d.getCurrentUrl().contains("/university/1"));

        WebElement reviewInput = wait.until(d -> d.findElement(By.cssSelector("textarea.review-input")));
        reviewInput.sendKeys("Great university!");

        WebElement star = wait.until(d -> d.findElement(By.cssSelector(".star-rating .star[data-value='5']")));
        star.click();

        WebElement postBtn = wait.until(d -> d.findElement(By.cssSelector("button.post-review")));
        postBtn.click();

        // Wait for review to appear
        wait.until(d -> d.findElement(By.xpath("//*[contains(text(),'Great university!')]")));
    }
}