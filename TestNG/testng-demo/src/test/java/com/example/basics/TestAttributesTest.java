package com.example.basics;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * =============================================================
 * СЛАЙД 4: Атрибуты аннотации @Test
 * =============================================================
 *
 * Демонстрирует основные атрибуты @Test:
 *
 *   priority          — порядок выполнения (меньше = раньше; default = 0)
 *   enabled           — включить / отключить тест (false = тест пропускается)
 *   timeOut           — максимальное время выполнения в мс; при превышении — FAIL
 *   invocationCount   — сколько раз вызывать тест
 *   expectedExceptions — тест PASS, только если выброшено это исключение
 *   description       — человекочитаемое описание теста
 */
public class TestAttributesTest {

    private UserService userService;

    @BeforeClass
    public void setUp() {
        userService = new UserService();
        userService.startServer();
    }

    @AfterClass
    public void tearDown() {
        userService.stopServer();
    }

    // -------------------------------------------------------------------------
    // priority: тесты выполняются по возрастанию значения.
    // Порядок здесь: priority=1 → priority=2 → priority=3
    // -------------------------------------------------------------------------

    @Test(priority = 1,
          description = "Сначала регистрируем пользователя")
    public void step1_registerUser() {
        System.out.println("  [priority=1] Регистрация пользователя alice");
        userService.register("alice", "pass1234");
        Assert.assertTrue(userService.userExists("alice"));
    }

    @Test(priority = 2,
          description = "Затем проверяем вход")
    public void step2_loginUser() {
        System.out.println("  [priority=2] Вход alice");
        Assert.assertTrue(userService.login("alice", "pass1234"));
    }

    @Test(priority = 3,
          description = "Наконец удаляем пользователя")
    public void step3_deleteUser() {
        System.out.println("  [priority=3] Удаление alice");
        Assert.assertTrue(userService.deleteUser("alice"));
        Assert.assertFalse(userService.userExists("alice"));
    }

    // -------------------------------------------------------------------------
    // enabled = false: тест не будет выполнен (статус SKIP в отчёте).
    // Удобно для временного отключения «сломанного» теста.
    // -------------------------------------------------------------------------

    @Test(enabled = false,
          description = "Этот тест отключён — он появится в отчёте как пропущенный")
    public void disabledTest() {
        Assert.fail("Этот код никогда не выполнится");
    }

    // -------------------------------------------------------------------------
    // timeOut: если метод работает дольше 500 мс — TestNG отмечает его FAIL.
    // -------------------------------------------------------------------------

    @Test(timeOut = 500,
          description = "Быстрая операция — должна уложиться в 500 мс")
    public void fastOperation() throws InterruptedException {
        System.out.println("  [timeOut=500ms] Быстрая операция...");
        Thread.sleep(50); // 50 мс — хорошо
        Assert.assertTrue(true);
    }

    // Раскомментируйте, чтобы увидеть FAIL по таймауту:
    // @Test(timeOut = 100, description = "Слишком медленная операция — FAIL")
    // public void slowOperation() throws InterruptedException {
    //     Thread.sleep(300); // 300 мс > 100 мс → TestNG выбросит ThreadTimeoutException
    // }

    // -------------------------------------------------------------------------
    // invocationCount: метод будет вызван указанное число раз.
    // Используется для стресс/нагрузочного тестирования.
    // -------------------------------------------------------------------------

    private int loginCallCount = 0;

    @Test(invocationCount = 3,
          description = "Метод вызывается 3 раза подряд")
    public void repeatedLogin() {
        loginCallCount++;
        System.out.println("  [invocationCount] Вызов #" + loginCallCount);
        // Здесь можно проверять поведение при повторных вызовах
        Assert.assertTrue(loginCallCount <= 3);
    }

    // -------------------------------------------------------------------------
    // expectedExceptions: тест PASS, если метод выбросил нужное исключение.
    // Если исключение НЕ выброшено — тест FAIL.
    // -------------------------------------------------------------------------

    @Test(expectedExceptions = IllegalArgumentException.class,
          description = "Регистрация с пустым логином должна выбросить IllegalArgumentException")
    public void registerWithEmptyLogin() {
        System.out.println("  [expectedExceptions] Попытка регистрации с пустым логином");
        userService.register("", "pass1234"); // должно выбросить IAE
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
          expectedExceptionsMessageRegExp = ".*короче 4.*",
          description = "Пароль < 4 символов — IAE с нужным сообщением")
    public void registerWithShortPassword() {
        System.out.println("  [expectedExceptions] Попытка регистрации с коротким паролем");
        userService.register("bob", "123"); // пароль < 4 → IAE
    }
}
