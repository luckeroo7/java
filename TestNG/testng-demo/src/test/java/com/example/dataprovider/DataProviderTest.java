package com.example.dataprovider;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * =============================================================
 * СЛАЙД 5: DataProvider и параметризация
 * =============================================================
 *
 * @DataProvider позволяет запускать один тестовый метод несколько раз
 * с разными наборами входных данных.
 *
 * Ключевые моменты:
 *   - Метод с @DataProvider возвращает Object[][]
 *   - Каждая строка (Object[]) — один запуск теста
 *   - Имя провайдера задаётся в name="..."
 *   - Тест ссылается на провайдер через @Test(dataProvider="...")
 *   - parallel=true — строки выполняются в параллельных потоках
 *   - DataProvider может жить в другом классе (dataProviderClass=...)
 */
public class DataProviderTest {

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

    // =========================================================================
    // DataProvider 1: валидные данные регистрации
    // =========================================================================

    /**
     * Провайдер возвращает пары {логин, пароль} для успешной регистрации.
     * Тест запустится 4 раза — по одному для каждой строки.
     */
    @DataProvider(name = "validUsers")
    public Object[][] validUsersProvider() {
        return new Object[][] {
            { "alice",   "pass1234" },
            { "bob",     "secureP@ss" },
            { "charlie", "Ch@rlie99" },
            { "diana",   "diana2024" },
        };
    }

    @Test(dataProvider = "validUsers",
          description = "Регистрация с валидными данными должна успешно создавать пользователя")
    public void testRegisterValidUser(String username, String password) {
        System.out.printf("  [DataProvider] Регистрация: username=%s, password=%s%n",
                username, password);
        userService.register(username, password);
        Assert.assertTrue(userService.userExists(username),
                "Пользователь " + username + " должен существовать после регистрации");
    }

    // =========================================================================
    // DataProvider 2: сценарии входа — ожидаемый результат передаётся как параметр
    // =========================================================================

    /**
     * Каждая строка: {логин, пароль, ожидаемый результат входа}.
     * Это позволяет тестировать как успешный, так и неудачный вход
     * одним методом.
     */
    @DataProvider(name = "loginScenarios")
    public Object[][] loginScenariosProvider() {
        return new Object[][] {
            // username,   password,     expectedResult
            { "alice",   "pass1234",   true  },   // правильный пароль → успех
            { "alice",   "wrongPass",  false },   // неверный пароль   → отказ
            { "alice",   "",           false },   // пустой пароль     → отказ
            { "unknown", "pass1234",   false },   // несуществующий    → отказ
        };
    }

    @Test(dataProvider = "loginScenarios",
          dependsOnMethods = "testRegisterValidUser",
          description = "Проверяем сценарии входа с разными данными")
    public void testLoginScenarios(String username, String password, boolean expected) {
        System.out.printf("  [DataProvider] Вход: username=%s, expected=%b%n",
                username, expected);
        boolean actual = userService.login(username, password);
        Assert.assertEquals(actual, expected,
                String.format("Вход для (%s / %s) должен быть %b", username, password, expected));
    }

    // =========================================================================
    // DataProvider 3: невалидные данные — ожидаем исключение
    // =========================================================================

    /**
     * Провайдер для проверки валидации: пары {логин, пароль},
     * которые должны вызвать IllegalArgumentException.
     */
    @DataProvider(name = "invalidRegistrations")
    public Object[][] invalidRegistrationsProvider() {
        return new Object[][] {
            { "",      "pass1234" },  // пустой логин
            { "  ",    "pass1234" },  // логин из пробелов
            { "valid", "123"      },  // пароль слишком короткий
            { "valid", null       },  // пароль null
        };
    }

    @Test(dataProvider = "invalidRegistrations",
          expectedExceptions = IllegalArgumentException.class,
          description = "Регистрация с невалидными данными должна выбрасывать исключение")
    public void testRegisterInvalidUser(String username, String password) {
        System.out.printf("  [DataProvider] Невалидная регистрация: username='%s', password='%s'%n",
                username, password);
        userService.register(username, password); // должно выбросить IAE
    }

    // =========================================================================
    // DataProvider 4: parallel=true — строки выполняются в нескольких потоках
    // =========================================================================

    /**
     * Параллельный DataProvider: каждая строка отдаётся в отдельный поток.
     * Это ускоряет выполнение, но тест должен быть потокобезопасным.
     */
    @DataProvider(name = "parallelData", parallel = true)
    public Object[][] parallelDataProvider() {
        return new Object[][] {
            { "p_user1", "pass0001" },
            { "p_user2", "pass0002" },
            { "p_user3", "pass0003" },
        };
    }

    @Test(dataProvider = "parallelData",
          description = "Параллельная регистрация — каждая строка в своём потоке")
    public synchronized void testParallelRegistration(String username, String password) {
        // synchronized — защищаем общий UserService от гонки данных
        System.out.printf("  [parallel DataProvider | %s] Регистрация: %s%n",
                Thread.currentThread().getName(), username);
        userService.register(username, password);
        Assert.assertTrue(userService.userExists(username));
    }
}
