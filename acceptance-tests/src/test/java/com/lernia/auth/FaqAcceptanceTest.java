package com.lernia.auth;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class FaqAcceptanceTest extends BaseAcceptanceTest {

    @Test
    public void testFaqPage_ATC02() {
        driver.get(baseUrl + "/");

        WebElement faqLink = findAny(
            By.linkText("FAQ"),
            By.partialLinkText("FAQ"),
            By.partialLinkText("Help"),
            By.xpath("//a[contains(translate(text(),'FAQS','faqs'),'faq')]"),
            By.cssSelector("footer a[href*='faq']"),
            By.cssSelector("header a[href*='faq']")
        );
        Assertions.assertNotNull(faqLink, "\"FAQ\" or \"Help / FAQs\" link not found in header or footer");

        faqLink.click();
        wait.until(d -> d.getCurrentUrl().toLowerCase().contains("/faqs"));
        String url = driver.getCurrentUrl().toLowerCase();
        System.out.println("Current URL after click: " + url);

        List<WebElement> questions = driver.findElements(
            By.cssSelector(".faq-q .q-text")
        );
        Assertions.assertFalse(questions.isEmpty(), "No FAQ questions found");

        WebElement firstQuestionButton = driver.findElement(By.cssSelector(".faq-q"));
        firstQuestionButton.click();

        List<WebElement> answers = driver.findElements(
            By.cssSelector(".faq-a.open .a-inner")
        );
        Assertions.assertFalse(answers.isEmpty(), "No FAQ answer became visible after clicking question");

        String qText = questions.get(0).getText();
        String aText = answers.get(0).getText();
        Assertions.assertTrue(qText.length() > 5, "FAQ question text too short");
        Assertions.assertTrue(aText.length() > 5, "FAQ answer text too short");

        Assertions.assertTrue(questions.get(0).isDisplayed(), "FAQ question not visible");
        Assertions.assertTrue(answers.get(0).isDisplayed(), "FAQ answer not visible");
    }
}