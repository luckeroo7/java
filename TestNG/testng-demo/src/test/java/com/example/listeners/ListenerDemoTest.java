package com.example.listeners;

import com.example.UserService;
import org.testng.Assert;
import org.testng.annotations.*;

/**
 * =============================================================
 * СЛАЙД 9: Подключение Listener через @Listeners
 * =============================================================
 *
 * Аннотация @Listeners подключает один или несколько слушателей
 * непосредственно к классу. Это удобно, когда нужен listener только
 * для отдельного класса, а не для всего сьюта.
 *
 * Для глобального подключения используйте testng.xml:
 *   <listeners>
 *     <listener class-name="com.example.listeners.CustomTestListener"/>
 *   </listeners>
 */
@Listeners(CustomTestListener.class)
public class ListenerDemoTest {

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

    @Test(description = "Успешный тест — listener выведет УСПЕХ")
    public void passingTest() {
        userService.register("listenerUser", "listen123");
        Assert.assertTrue(userService.userExists("listenerUser"));
    }

    @Test(description = "Этот тест намеренно падает — listener выведет ПРОВАЛ")
    public void failingTest() {
        // Намеренный провал для демонстрации onTestFailure в листенере
        Assert.assertEquals(
                userService.getUserCount(), 999,
                "Намеренный провал: счётчик не равен 999"
        );
    }

    @Test(enabled = false,
          description = "Пропущенный тест — listener выведет ПРОПУСК")
    public void skippedTest() {
        Assert.fail("Не должно выполняться");
    }
}
