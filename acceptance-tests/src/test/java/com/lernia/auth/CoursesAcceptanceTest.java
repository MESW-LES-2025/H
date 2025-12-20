package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CoursesAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void testViewSingleCoursePage_ATC15() {
        // Step 1: Go directly to course/1 (no login)
        driver.get(baseUrl + "/course/1");

        // Step 2: Wait for the course page to load
        wait.until(d -> d.getCurrentUrl().contains("/course/1"));

        // Step 3: Check that the course details are present
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
        // Step 1: Go to /courses (no login)
        driver.get(baseUrl + "/courses");
        wait.until(d -> d.getCurrentUrl().contains("/courses"));

        // Step 2: Find the search input and enter a course name (e.g., "Architecture")
        WebElement searchInput = wait.until(d -> findAny(
            By.cssSelector("input.hero-input"),
            By.cssSelector("input[placeholder*='course']"),
            By.xpath("//input[contains(@placeholder,'course')]")
        ));
        Assertions.assertNotNull(searchInput, "Course search input not found");

        searchInput.clear();
        searchInput.sendKeys("Architecture");

        // Step 3: Always click the search button and wait for it to be clickable
        WebElement searchBtn = wait.until(d -> findAny(
            By.cssSelector("button.search-btn"),
            By.xpath("//button[contains(text(),'Search')]")
        ));
        Assertions.assertNotNull(searchBtn, "Search button not found");
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable(searchBtn));
        searchBtn.click();

        // Step 4: Wait until all visible course cards match the search or "no results" is shown
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

        // Step 5: Assert that all visible course cards contain "Architecture" in the name (case-insensitive)
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

    @Test
    public void testFilterCourses_ATC17() {
        // Step 1: Go to /courses 
        driver.get(baseUrl + "/courses");
        wait.until(d -> d.getCurrentUrl().contains("/courses"));

        // Step 2: Open filters and set values
        // Type: MASTER
        WebElement masterCheckbox = wait.until(d -> findAny(
            By.xpath("//label[contains(.,'MASTER')]/input[@type='checkbox']"),
            By.xpath("//input[@type='checkbox' and @value='MASTER']")
        ));
        Assertions.assertNotNull(masterCheckbox, "MASTER type checkbox not found");
        if (!masterCheckbox.isSelected()) masterCheckbox.click();

        // Max cost
        WebElement costInput = wait.until(d -> findAny(
            By.cssSelector("input[formcontrolname='costMax']"),
            By.xpath("//input[@placeholder='e.g. 3000']")
        ));
        Assertions.assertNotNull(costInput, "Max cost input not found");
        costInput.clear();
        costInput.sendKeys("2000");

        // Language: English
        WebElement englishCheckbox = wait.until(d -> findAny(
            By.xpath("//label[contains(.,'English')]/input[@type='checkbox']"),
            By.xpath("//input[@type='checkbox' and @value='English']")
        ));
        Assertions.assertNotNull(englishCheckbox, "English language checkbox not found");
        if (!englishCheckbox.isSelected()) englishCheckbox.click();

        // Country: Sweden
        WebElement swedenCheckbox = wait.until(d -> findAny(
            By.xpath("//label[contains(.,'Sweden')]/input[@type='checkbox']"),
            By.xpath("//input[@type='checkbox' and @value='Sweden']")
        ));
        Assertions.assertNotNull(swedenCheckbox, "Sweden country checkbox not found");
        if (!swedenCheckbox.isSelected()) swedenCheckbox.click();

        // Step 3: Wait for the filter to apply (all visible cards must match)
        wait.until(d -> {
            var cards = d.findElements(By.cssSelector(".course-card"));
            if (cards.isEmpty()) {
                // No cards, check for empty message
                return !d.findElements(By.cssSelector(".empty")).isEmpty();
            }
            for (WebElement card : cards) {
                // Type
                boolean typeOk = card.findElements(By.xpath(".//span[contains(@class,'badge') and contains(.,'MASTER')]")).size() > 0;
                // Cost
                boolean costOk = true;
                try {
                    String priceText = card.findElement(By.cssSelector(".price-value")).getText().replaceAll("[^\\d]", "");
                    int price = Integer.parseInt(priceText);
                    costOk = price <= 2000;
                } catch (Exception ignored) {}
                // Language
                boolean langOk = card.findElements(By.xpath(".//div[contains(@class,'meta')]//div[contains(.,'English')]")).size() > 0;
                // Country
                boolean countryOk = card.findElements(By.xpath(".//p[contains(@class,'uni') and contains(.,'Sweden')]")).size() > 0;

                if (!(typeOk && costOk && langOk && countryOk)) {
                    return false;
                }
            }
            return true;
        });

        // Step 4: Assert that all visible course cards match the filters
        boolean found = false;
        for (WebElement card : driver.findElements(By.cssSelector(".course-card"))) {
            boolean typeOk = card.findElements(By.xpath(".//span[contains(@class,'badge') and contains(.,'MASTER')]")).size() > 0;
            boolean costOk = true;
            try {
                String priceText = card.findElement(By.cssSelector(".price-value")).getText().replaceAll("[^\\d]", "");
                int price = Integer.parseInt(priceText);
                costOk = price <= 2000;
            } catch (Exception ignored) {}
            boolean langOk = card.findElements(By.xpath(".//div[contains(@class,'meta')]//div[contains(.,'English')]")).size() > 0;
            boolean countryOk = card.findElements(By.xpath(".//p[contains(@class,'uni') and (contains(.,'Sweden') or contains(.,'Stockholm'))]")).size() > 0;
            Assertions.assertTrue(typeOk, "Course type is not MASTER");
            Assertions.assertTrue(costOk, "Course cost is above 2000");
            Assertions.assertTrue(langOk, "Course language is not English");
            Assertions.assertTrue(countryOk, "Course country is not Sweden");
            found = true;
        }

        // If no cards, check for "No courses found" message
        if (!found) {
            WebElement emptyMsg = findAny(
                By.cssSelector(".empty"),
                By.xpath("//*[contains(text(),'No courses found')]"),
                By.xpath("//*[contains(text(),'No courses match your criteria')]")
            );
            Assertions.assertNotNull(emptyMsg, "No courses found message should be displayed if no results");
        }
    }

    @Test
    public void testViewCourseDetails_ATC24() {
        // Scenario: View Course Details (US16)
        // 1. Go to /course/1 
        driver.get(baseUrl + "/course/1");
        wait.until(d -> d.getCurrentUrl().contains("/course/1"));

        // 2. Assert course name is displayed
        WebElement name = findAny(
            By.cssSelector(".course-name"),
            By.xpath("//h1"),
            By.xpath("//h2")
        );
        Assertions.assertNotNull(name, "Course name not found on course page");

        // 3. Assert university, cost, duration, curriculum are displayed
        WebElement university = findAny(
            By.cssSelector("p.h4.mb-0"),
            By.xpath("//p[contains(@class,'h4') and contains(@class,'mb-0')]")
        );
        Assertions.assertNotNull(university, "University not found on course page");

        WebElement cost = findAny(By.cssSelector(".course-cost"), By.xpath("//*[contains(text(),'Cost')]"));
        Assertions.assertNotNull(cost, "Course cost not found");

        WebElement duration = findAny(By.cssSelector(".course-duration"), By.xpath("//*[contains(text(),'Duration')]"));
        Assertions.assertNotNull(duration, "Course duration not found");
    }

    @Test
    public void testAddCourseToFavorites_ATC30() {
        // Scenario: Add Course to Favorites (US20)
        loginAsTestUser();

        driver.get(baseUrl + "/course/2");
        String currentUrl = driver.getCurrentUrl();
        System.out.println("Navigated to: " + currentUrl);

        Assertions.assertTrue(
            currentUrl.contains("/course/2"),
            "Failed to open course page: expected URL to contain /course/2 but was " + currentUrl
        );

        try {
            wait.until(d -> d.findElement(By.cssSelector(".container-course")));
            System.out.println("Course page loaded!");
        } catch (Exception e) {
            System.out.println("Course page did not load. Page source:\n" + driver.getPageSource());
            Assertions.fail("Course page did not load: .container-course not found");
        }

        WebElement favBtn = wait.until(d -> d.findElement(By.cssSelector("button.btn.btn-save-course")));
        favBtn.click();

        WebElement favIcon = favBtn.findElement(By.cssSelector("i"));
        wait.until(d -> favIcon.getAttribute("class").contains("bi-heart-fill"));

        Assertions.assertTrue(
            favIcon.getAttribute("class").contains("bi-heart-fill"),
            "Favorite icon did not become filled"
        );

        WebElement profileBtn = wait.until(d -> d.findElement(By.cssSelector(".navbar-actions .icon-btn[aria-label='Profile']")));
        profileBtn.click();
        wait.until(d -> d.getCurrentUrl().contains("/profile"));

        WebElement coursesTab = wait.until(d -> findAny(
            By.xpath("//button[contains(.,'Courses')]")
        ));
        coursesTab.click();
        String expectedCourseName = "MEng Aeronautical Engineering"; 
        wait.until(d -> {
            for (WebElement card : d.findElements(By.cssSelector(".course-card"))) {
                    WebElement title = card.findElement(By.cssSelector(".title"));
                    if (title.getText().trim().equalsIgnoreCase(expectedCourseName)) {
                        return true;
                    }
            }
            return false;
        });

        boolean found = false;
        for (WebElement card : driver.findElements(By.cssSelector(".course-card"))) {
            try {
                WebElement title = card.findElement(By.cssSelector(".title"));
                if (title.getText().trim().equalsIgnoreCase(expectedCourseName)) {
                    found = true;
                    break;
                }
            } catch (Exception ignored) {}
        }
        Assertions.assertTrue(found, "Course not found in favorites after adding");
    }
}