import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Конфигурация базы данных
class DatabaseConfig {
    private static volatile HikariDataSource dataSource; // volatile для потокобезопасности

    private DatabaseConfig() {} // Предотвращаем создание экземпляров

    public static synchronized HikariDataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/shop_db");
            config.setUsername("postgres");
            config.setPassword("admin");
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(20000);
            config.setMinimumIdle(2);
            config.setIdleTimeout(300000);
            config.setPoolName("ShopHikariPool");

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}

// Доменная модель
class Order {
    private Long id;
    private Long customerId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private LocalDateTime createdAt;

    public Order(Long customerId, BigDecimal totalAmount, String deliveryAddress) {
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.deliveryAddress = deliveryAddress;
        this.status = OrderStatus.PENDING;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("Order{id=%d, customerId=%d, status=%s, totalAmount=%s, address='%s'}",
                id, customerId, status, totalAmount, deliveryAddress);
    }
}

enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}

// Data Access Object
class OrderDAO {
    private final DataSource dataSource;

    public OrderDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long save(Order order) {
        String sql = "INSERT INTO orders (customer_id, status, total_amount, " +
                "delivery_address, created_at) " +
                "VALUES (?, ?, ?, ?, NOW()) RETURNING id";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, order.getCustomerId());
            pstmt.setString(2, order.getStatus().toString());
            pstmt.setBigDecimal(3, order.getTotalAmount());
            pstmt.setString(4, order.getDeliveryAddress());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long id = rs.getLong("id");
                    order.setId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось сохранить заказ", e);
        }
        return -1;
    }

    public Optional<Order> findById(long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToOrder(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось найти заказ по id: " + id, e);
        }
        return Optional.empty();
    }

    public List<Order> findByCustomerId(long customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ? " +
                "ORDER BY created_at DESC";
        List<Order> orders = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить заказы клиента: " + customerId, e);
        }

        return orders;
    }

    public void updateStatus(long orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status.toString());
            pstmt.setLong(2, orderId);

            int updated = pstmt.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Заказ не найден с id: " + orderId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось обновить статус заказа", e);
        }
    }

    public void delete(long orderId) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, orderId);
            int deleted = pstmt.executeUpdate();
            if (deleted == 0) {
                throw new RuntimeException("Заказ не найден с id: " + orderId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось удалить заказ", e);
        }
    }

    public OrderStats getStats() {
        String sql = "SELECT COUNT(*) as total_orders, " +
                "COALESCE(SUM(total_amount), 0) as total_revenue " +
                "FROM orders";

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new OrderStats(
                        rs.getLong("total_orders"),
                        rs.getBigDecimal("total_revenue")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось получить статистику заказов", e);
        }
        return new OrderStats(0, BigDecimal.ZERO);
    }

    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order(
                rs.getLong("customer_id"),
                rs.getBigDecimal("total_amount"),
                rs.getString("delivery_address")
        );
        order.setId(rs.getLong("id"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));

        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            order.setCreatedAt(timestamp.toLocalDateTime());
        }
        return order;
    }

    // DTO для статистики
    public static class OrderStats {
        public final long totalOrders;
        public final BigDecimal totalRevenue;

        public OrderStats(long totalOrders, BigDecimal totalRevenue) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
        }
    }
}

// Главное приложение
public class ShopApplication {
    public static void main(String[] args) {
        try {
            // 1. Инициализируем DataSource
            DataSource ds = DatabaseConfig.getDataSource();
            System.out.println("Пул соединений HikariCP инициализирован");

            // 2. Запускаем миграции Flyway
            Flyway flyway = Flyway.configure()
                    .dataSource(ds)
                    .locations("classpath:db/migration")  // ← должно быть ТОЧНО так
                    .load();
            flyway.migrate();
            System.out.println("Миграции выполнены");

            // 3. Демонстрация CRUD операций
            OrderDAO orderDAO = new OrderDAO(ds);

            // CREATE — создаем заказ
            Order order = new Order(
                    1L,
                    new BigDecimal("1599.99"),
                    "ул. Пушкина, 10"
            );
            long orderId = orderDAO.save(order);
            System.out.println("Заказ создан: ID=" + orderId);

            // READ — получаем по ID
            Optional<Order> found = orderDAO.findById(orderId);
            found.ifPresent(o -> System.out.println("Найден заказ: " + o));

            // UPDATE — меняем статус
            orderDAO.updateStatus(orderId, OrderStatus.SHIPPED);
            System.out.println("Статус изменен на SHIPPED");

            // LIST — получаем все заказы клиента
            List<Order> customerOrders = orderDAO.findByCustomerId(1L);
            System.out.println("Заказов у клиента: " + customerOrders.size());

            // STATS — считаем статистику
            OrderDAO.OrderStats stats = orderDAO.getStats();
            System.out.println("Всего заказов: " + stats.totalOrders);
            System.out.println("Общая выручка: " + stats.totalRevenue);

            // DELETE — удаляем заказ
            orderDAO.delete(orderId);
            System.out.println("Заказ удален");

        } catch (Exception e) {
            System.err.println("Ошибка приложения: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConfig.closeDataSource();
            System.out.println("Пул соединений закрыт");
        }
    }
}