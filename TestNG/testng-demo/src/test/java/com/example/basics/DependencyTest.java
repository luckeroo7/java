package com.example.basics;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * =============================================================
 * СЛАЙД 8: Зависимости между тестами (dependsOnMethods)
 * =============================================================
 *
 * TestNG позволяет явно указывать порядок выполнения через зависимости:
 *
 *   @Test(dependsOnMethods = {"method1", "method2"})
 *
 * Правила:
 *   - Если зависимый тест FAIL или SKIP → зависимый тест автоматически SKIP
 *   - alwaysRun = true: запустить тест даже если зависимость упала
 *   - dependsOnGroups: зависимость от целой группы
 *
 * Пример ниже моделирует реалистичный сценарий:
 *   Запуск сервера → Регистрация → Вход → Работа с данными → Выход → Остановка
 */
public class DependencyTest {

    private UserService userService;
    private boolean loginSuccessful = false;

    @BeforeClass
    public void initService() {
        userService = new UserService();
    }

    // =========================================================================
    // Цепочка зависимостей: шаг 1
    // =========================================================================

    @Test(priority = 1,
          description = "Шаг 1: Запуск сервера — базовая предпосылка для всего остального")
    public void step1_startServer() {
        System.out.println("\n  [STEP 1] Запуск сервера");
        userService.startServer();
        Assert.assertTrue(userService.isServerRunning(), "Сервер должен быть запущен");
    }

    // =========================================================================
    // Шаг 2 зависит от шага 1.
    // Если step1_startServer FAIL → step2_register будет SKIP
    // =========================================================================

    @Test(dependsOnMethods = {"step1_startServer"},
          description = "Шаг 2: Регистрация — требует запущенного сервера")
    public void step2_registerUser() {
        System.out.println("  [STEP 2] Регистрация пользователя");
        userService.register("alice", "alice1234");
        Assert.assertTrue(userService.userExists("alice"),
                "После регистрации пользователь должен существовать");
    }

    // =========================================================================
    // Шаг 3 зависит от шага 2.
    // Невозможно войти, если не зарегистрировались.
    // =========================================================================

    @Test(dependsOnMethods = {"step2_registerUser"},
          description = "Шаг 3: Вход — требует успешной регистрации")
    public void step3_loginUser() {
        System.out.println("  [STEP 3] Вход пользователя");
        loginSuccessful = userService.login("alice", "alice1234");
        Assert.assertTrue(loginSuccessful, "Вход должен быть успешным");
    }

    // =========================================================================
    // Шаг 4 зависит от шага 3.
    // =========================================================================

    @Test(dependsOnMethods = {"step3_loginUser"},
          description = "Шаг 4: Бизнес-операция — доступна только авторизованным")
    public void step4_performAction() {
        System.out.println("  [STEP 4] Выполнение действия авторизованным пользователем");
        Assert.assertTrue(loginSuccessful, "Нужно быть залогиненным");
        // Регистрируем ещё одного пользователя (имитация действия)
        userService.register("bob", "bob1234");
        Assert.assertEquals(userService.getUserCount(), 2);
    }

    // =========================================================================
    // Шаг 5: alwaysRun = true
    // Очистка запускается ВСЕГДА, даже если предыдущие шаги упали.
    // Аналог блока `finally` в try/catch.
    // =========================================================================

    @Test(dependsOnMethods = {"step1_startServer"},
          alwaysRun = true,
          description = "Шаг 5 (cleanup): Останавливаем сервер в любом случае")
    public void step5_stopServer() {
        System.out.println("  [STEP 5 — alwaysRun] Остановка сервера");
        userService.stopServer();
        Assert.assertFalse(userService.isServerRunning(), "Сервер должен быть остановлен");
    }

    // =========================================================================
    // Демонстрация SKIP: этот тест зависит от несуществующего метода (в реальном
    // проекте это может быть намеренно пропущенный сценарий).
    // Раскомментируйте, чтобы увидеть SKIP в отчёте:
    // =========================================================================

    // @Test(dependsOnMethods = {"methodThatDoesNotExist"})
    // public void skippedTest() {
    //     Assert.fail("Этот код никогда не выполнится");
    // }
}
