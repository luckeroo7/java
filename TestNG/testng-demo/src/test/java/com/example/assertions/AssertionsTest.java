package com.example.assertions;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

import java.util.Arrays;
import java.util.List;

/**
 * =============================================================
 * Проверки (Assert) и SoftAssert
 * =============================================================
 *
 * TestNG предоставляет два вида проверок:
 *
 * 1. Assert (жёсткий) — первый провальный assert СРАЗУ останавливает тест.
 *    Последующие проверки НЕ выполняются.
 *
 * 2. SoftAssert (мягкий) — все проверки выполняются до конца.
 *    Все ошибки накапливаются и выдаются вместе при вызове assertAll().
 *    Это позволяет за один прогон увидеть ВСЕ проблемы, а не только первую.
 *
 * Когда использовать:
 *   Assert      — когда дальнейший тест бессмысленен без предыдущей проверки
 *   SoftAssert  — когда нужно собрать все дефекты за один запуск
 *                 (например, проверка всех полей ответа API)
 */
public class AssertionsTest {

    private UserService userService;

    @BeforeClass
    public void setUp() {
        userService = new UserService();
        userService.startServer();
        userService.register("alice", "pass1234");
        userService.register("bob",   "bob5678");
    }

    @AfterClass
    public void tearDown() {
        userService.stopServer();
    }

    // =========================================================================
    // Жёсткие проверки (Assert)
    // =========================================================================

    @Test(description = "Assert.assertEquals — сравнение значений")
    public void assertEqualsDemo() {
        int expected = 2;
        int actual   = userService.getUserCount();
        // Сигнатура: assertEquals(actual, expected, "сообщение при провале")
        Assert.assertEquals(actual, expected, "Должно быть 2 пользователя");
    }

    @Test(description = "Assert.assertTrue / assertFalse")
    public void assertTrueFalseDemo() {
        Assert.assertTrue(userService.isServerRunning(),
                "Сервер должен быть запущен");
        Assert.assertFalse(userService.userExists("ghost"),
                "Пользователь 'ghost' не должен существовать");
    }

    @Test(description = "Assert.assertNotNull / assertNull")
    public void assertNullDemo() {
        // assertNotNull: объект НЕ должен быть null
        String name = "alice";
        Assert.assertNotNull(name, "Имя не должно быть null");

        // assertNull: объект ДОЛЖЕН быть null
        String absent = null;
        Assert.assertNull(absent, "Ожидаем null для отсутствующего значения");
    }

    @Test(description = "Assert.assertThrows — проверка исключения inline")
public void assertThrowsDemo() {
    Assert.assertThrows(
        IllegalArgumentException.class,
        () -> userService.register("", "pass1234")
);
}

    @Test(description = "Assert для коллекций: assertEquals, assertEqualsNoOrder")
    public void assertCollectionsDemo() {
        List<String> expected = Arrays.asList("hello", "world");
        List<String> actual   = Arrays.asList("hello", "world");

        // assertEquals — порядок важен
        Assert.assertEquals(actual, expected);

        // assertEqualsNoOrder — порядок НЕ важен
        Object[] arr1 = {"b", "a", "c"};
        Object[] arr2 = {"c", "b", "a"};
        Assert.assertEqualsNoOrder(arr1, arr2, "Массивы должны содержать одинаковые элементы");
    }

    // =========================================================================
    // SoftAssert — «мягкие» проверки
    // =========================================================================

    @Test(description = "SoftAssert: все ошибки собираются и выдаются вместе")
    public void softAssertDemo() {
        SoftAssert soft = new SoftAssert();

        // Проверяем несколько условий. Даже если одно провалится —
        // остальные тоже будут проверены.
        soft.assertTrue(userService.isServerRunning(),   "Сервер запущен");
        soft.assertTrue(userService.userExists("alice"),  "Alice существует");
        soft.assertTrue(userService.userExists("bob"),    "Bob существует");
        soft.assertFalse(userService.userExists("carol"), "Carol НЕ существует");
        soft.assertEquals(userService.getUserCount(), 2,  "Всего 2 пользователя");

        // ОБЯЗАТЕЛЬНО: без этого вызова тест всегда будет PASS, даже если были ошибки!
        soft.assertAll();
    }

    /**
     * Пример: проверка объекта-результата — все поля сразу.
     * SoftAssert идеален для API-ответов, DTO, сущностей с множеством полей.
     */
    @Test(description = "SoftAssert: проверка нескольких полей объекта за раз")
    public void softAssertApiResponseDemo() {
        // Имитируем «ответ» сервиса (простой record / map)
        String status  = "OK";
        int    code    = 200;
        String body    = "User registered";
        boolean cached = false;

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(status, "OK",               "HTTP статус");
        soft.assertEquals(code,   200,                "HTTP код");
        soft.assertNotNull(body,                      "Тело ответа не null");
        soft.assertFalse(cached,                      "Ответ не из кеша");

        soft.assertAll(); // покажет ВСЕ проблемы, если они есть
    }
}
