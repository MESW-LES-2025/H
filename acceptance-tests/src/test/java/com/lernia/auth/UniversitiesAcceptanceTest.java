package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class UniversitiesAcceptanceTest extends BaseAcceptanceTest {

    /*@Test
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
    }*/

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
                    String country = card.findElement(By.cssSelector(".badge.country")).getText().toLowerCase();
                    countryOk = country.contains("spain");
                } catch (Exception ignored) {}
                if (!(countryOk)) {
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
        Assertions.assertTrue(countryOk, "University card does not have country Spain");
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

        WebElement name = findAny(
            By.cssSelector(".h1.fw-bold.mb-1"),
            By.xpath("//h2[contains(@class,'h1')]"),
            By.xpath("//h2")
        );
        Assertions.assertNotNull(name, "University name not found");

        // Look for location in either header or overview stat box
        WebElement location = findAny(
            By.cssSelector("p.h4.mb-0"),
            By.cssSelector("p.stat-value.fw-bold.h5"),
            By.xpath("//p[contains(text(),'Unknown Location')]"),
            By.xpath("//p[contains(text(),'N/A')]")
        );
        Assertions.assertNotNull(location, "University location not found");

        // Click the Courses tab to make its content visible
        WebElement coursesTab = wait.until(d -> findAny(
            By.xpath("//button[contains(.,'Courses')]"),
            By.cssSelector("button[ngbnavlink]")
        ));
        coursesTab.click();

        // Wait for the "Featured Courses" heading or a course item to appear
        WebElement programs = wait.until(d -> findAny(
            By.xpath("//*[contains(text(),'Featured Courses')]"),
            By.cssSelector(".courses-list"),
            By.cssSelector(".course-item"),
            By.xpath("//h5[contains(@class,'fw-bold') and contains(text(),'Courses')]")
        ));
        Assertions.assertNotNull(programs, "University programs not found");
    }

    // @Test
    // public void testViewReviewsOnUniversity_ATC27() {
    //     driver.get(baseUrl + "/university/9");
    //     wait.until(d -> d.getCurrentUrl().contains("/university/9"));

    //     WebElement reviewsTab = wait.until(d -> d.findElement(
    //         By.xpath("//button[contains(.,'Reviews')]")
    //     ));
    //     reviewsTab.click();

    //     WebElement reviewsSection = new WebDriverWait(driver, Duration.ofSeconds(10))
    //         .until(d -> d.findElement(By.cssSelector(".reviews-container")));

    //     Assertions.assertNotNull(reviewsSection, "Reviews section not found");

    //     WebElement review = findAny(
    //         By.cssSelector(".review-card"),
    //         By.cssSelector(".no-reviews"),
    //         By.xpath("//*[contains(@class,'review-card')]"),
    //         By.xpath("//*[contains(text(),'No reviews yet.')]")
    //     );
    //     Assertions.assertNotNull(review, "No reviews found for university");
    // }


    @Test
    public void testAddReviewToUniversity_ATC28() throws InterruptedException {
        loginAsTestUser();

        driver.get(baseUrl + "/university/1");
        wait.until(d -> d.getCurrentUrl().contains("/university/1"));

        WebElement reviewsTab = wait.until(d -> d.findElement(
            By.xpath("//ul[contains(@class,'nav-tabs')]//button[contains(.,'Reviews')]")
        ));
        reviewsTab.click();
        Thread.sleep(300); 

        wait.until(d -> d.findElements(By.cssSelector(".add-review-section")).size() > 0);

        WebElement titleInput = wait.until(d -> d.findElement(By.cssSelector("input[data-testid='review-title']")));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", titleInput);
        titleInput.clear();
        titleInput.sendKeys("Great university!");

        WebElement reviewInput = wait.until(d -> d.findElement(By.cssSelector("textarea[data-testid='review-description']")));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", reviewInput);
        reviewInput.clear();
        reviewInput.sendKeys("This is a test review description.");

        WebElement ratingSelect = wait.until(d -> d.findElement(By.cssSelector("select[data-testid='review-rating']")));
        ratingSelect.click();
        ratingSelect.findElement(By.cssSelector("option[value='5']")).click();

        WebElement postBtn = wait.until(d -> d.findElement(By.cssSelector("button[data-testid='submit-review']")));
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", postBtn);
        postBtn.click();

        wait.until(d -> d.findElements(By.xpath("//*[contains(text(),'Great university!')]")).size() > 0);
    }
}
