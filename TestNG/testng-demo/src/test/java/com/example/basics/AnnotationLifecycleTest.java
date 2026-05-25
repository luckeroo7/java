package com.example.basics;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * =============================================================
 * СЛАЙД 3: Аннотации TestNG
 * =============================================================
 *
 * Демонстрирует порядок вызова конфигурационных аннотаций:
 *
 *   @BeforeSuite  — один раз перед всем набором тестов (сьютом)
 *   @AfterSuite   — один раз после всего набора
 *   @BeforeClass  — один раз перед первым тестом в этом классе
 *   @AfterClass   — один раз после последнего теста в классе
 *   @BeforeMethod — перед КАЖДЫМ тестовым методом
 *   @AfterMethod  — после КАЖДОГО тестового метода
 *
 * Порядок вызова:
 *   BeforeSuite → BeforeClass → [BeforeMethod → @Test → AfterMethod] × N → AfterClass → AfterSuite
 */
public class AnnotationLifecycleTest {

    private UserService userService;

    // -------------------------------------------------------------------------
    // BeforeSuite / AfterSuite
    // Выполняется один раз для всего <suite> в testng.xml.
    // Подходит для запуска общей инфраструктуры: БД, Docker-контейнеры и т.п.
    // -------------------------------------------------------------------------

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("\n========================================");
        System.out.println("@BeforeSuite: Инициализация всего сьюта");
        System.out.println("========================================");
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("\n========================================");
        System.out.println("@AfterSuite: Завершение всего сьюта");
        System.out.println("========================================\n");
    }

    // -------------------------------------------------------------------------
    // BeforeClass / AfterClass
    // Выполняется один раз для данного класса.
    // -------------------------------------------------------------------------

    @BeforeClass
    public void beforeClass() {
        System.out.println("\n  @BeforeClass: Создание UserService для класса");
        userService = new UserService();
        userService.startServer(); // запускаем сервер один раз для всего класса
    }

    @AfterClass
    public void afterClass() {
        System.out.println("  @AfterClass: Остановка сервера\n");
        userService.stopServer();
    }

    // -------------------------------------------------------------------------
    // BeforeMethod / AfterMethod
    // Выполняется до и после КАЖДОГО теста.
    // Здесь удобно готовить «чистое» состояние для каждого теста.
    // -------------------------------------------------------------------------

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("    @BeforeMethod: Регистрация тестового пользователя");
        userService.register("testUser", "secret");
    }

    @AfterMethod
    public void afterMethod() {
        System.out.println("    @AfterMethod: Удаление тестового пользователя");
        userService.deleteUser("testUser");
    }

    // =========================================================================
    // Тесты
    // =========================================================================

    @Test
    public void testServerIsRunning() {
        System.out.println("      @Test: testServerIsRunning");
        Assert.assertTrue(userService.isServerRunning(),
                "Сервер должен быть запущен после @BeforeClass");
    }

    @Test
    public void testUserExists() {
        System.out.println("      @Test: testUserExists");
        Assert.assertTrue(userService.userExists("testUser"),
                "Пользователь должен существовать после @BeforeMethod");
    }

    @Test
    public void testLoginSuccess() {
        System.out.println("      @Test: testLoginSuccess");
        boolean result = userService.login("testUser", "secret");
        Assert.assertTrue(result, "Вход с правильными данными должен быть успешным");
    }

    @Test
    public void testLoginWrongPassword() {
        System.out.println("      @Test: testLoginWrongPassword");
        boolean result = userService.login("testUser", "wrong");
        Assert.assertFalse(result, "Вход с неверным паролем должен быть отклонён");
    }
}
