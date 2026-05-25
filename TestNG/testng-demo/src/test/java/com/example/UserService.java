package com.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Простой сервис управления пользователями.
 * Это «продакшн-код», который мы будем тестировать с помощью TestNG.
 */
public class UserService {

    private final Map<String, String> users = new HashMap<>();
    private boolean serverRunning = false;

    /** Запустить «сервер» (имитация) */
    public void startServer() {
        serverRunning = true;
        System.out.println("  [UserService] Сервер запущен");
    }

    /** Остановить «сервер» */
    public void stopServer() {
        serverRunning = false;
        System.out.println("  [UserService] Сервер остановлен");
    }

    public boolean isServerRunning() {
        return serverRunning;
    }

    /**
     * Регистрация пользователя.
     * @throws IllegalStateException если сервер не запущен
     * @throws IllegalArgumentException если логин или пароль пустые
     */
    public void register(String username, String password) {
        if (!serverRunning) {
            throw new IllegalStateException("Сервер не запущен");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым");
        }
        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("Пароль должен быть не короче 4 символов");
        }
        users.put(username, password);
    }

    /**
     * Вход пользователя.
     * @return true — успешно, false — неверные данные
     */
    public boolean login(String username, String password) {
        if (!serverRunning) {
            throw new IllegalStateException("Сервер не запущен");
        }
        return password.equals(users.get(username));
    }

    /**
     * Удаление пользователя.
     * @return true — пользователь удалён, false — пользователь не найден
     */
    public boolean deleteUser(String username) {
        if (!serverRunning) {
            throw new IllegalStateException("Сервер не запущен");
        }
        return users.remove(username) != null;
    }

    public int getUserCount() {
        return users.size();
    }

    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}
