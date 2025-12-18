package com.lernia.auth;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AboutUsAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void testAboutUsPage_ATC01() {
        driver.get(baseUrl + "/");
        Assertions.assertFalse(driver.getTitle().toLowerCase().contains("error"), "Landing page did not load successfully");

        WebElement aboutLink = findAny(
            By.linkText("About Us"),
            By.partialLinkText("About"),
            By.xpath("//a[contains(translate(text(),'ABOUT','about'),'about')]"),
            By.cssSelector("footer a[href*='about']"),
            By.cssSelector("header a[href*='about']")
        );
        Assertions.assertNotNull(aboutLink, "\"About Us\" link not found in header or footer");

        aboutLink.click();
        boolean onAboutPage = driver.getCurrentUrl().toLowerCase().contains("/about");
        Assertions.assertTrue(onAboutPage, "Did not navigate to About Us page");

        // ER3.1: Mission or purpose
        WebElement mission = findAny(
            By.xpath("//*[contains(translate(text(),'MISSION','mission'),'mission')]"),
            By.xpath("//*[contains(translate(text(),'PURPOSE','purpose'),'purpose')]"),
            By.cssSelector(".about-mission"),
            By.cssSelector(".mission")
        );
        Assertions.assertNotNull(mission, "Mission or purpose not found on About Us page");

        // ER3.2: Team or organisation info
        WebElement team = findAny(
            By.xpath("//*[contains(translate(text(),'TEAM','team'),'team')]"),
            By.xpath("//*[contains(translate(text(),'CREATOR','creator'),'creator')]"),
            By.xpath("//*[contains(translate(text(),'ORGANISATION','organisation'),'organisation')]"),
            By.cssSelector(".about-team"),
            By.cssSelector(".team")
        );
        Assertions.assertNotNull(team, "Team or organisation info not found on About Us page");

        // // ER3.3: At least one contact or reference link
        // WebElement contact = findAny(
        //     By.xpath("//a[contains(@href,'mailto:')]"),
        //     By.xpath("//a[contains(@href,'contact')]"),
        //     By.xpath("//a[contains(text(),'Contact')]"),
        //     By.cssSelector("a[href*='contact']")
        // );
        //Assertions.assertNotNull(contact, "Contact or reference link not found on About Us page");
    }
}