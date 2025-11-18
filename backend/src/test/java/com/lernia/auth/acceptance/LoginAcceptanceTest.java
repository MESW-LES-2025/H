package com.lernia.auth.acceptance;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginAcceptanceTest {

    private WebDriver driver;

    @Before
    public void setUp() {
        // Configure o caminho para o chromedriver na sua máquina/local
        System.setProperty("webdriver.chrome.driver", "/caminho/para/chromedriver");

        // Inicializa o driver Chrome
        driver = new ChromeDriver();
    }

    @Test
    public void testLoginPageTitle() {
        // Abre a página de login localmente (ajuste a URL conforme seu ambiente)
        driver.get("http://localhost:8080/login");

        // Verifica se o título da página é o esperado
        String title = driver.getTitle();
        assertEquals("Página de Login", title);
    }

    @After
    public void tearDown() {
        // Fecha o navegador após o teste
        if (driver != null) {
            driver.quit();
        }
    }
}
