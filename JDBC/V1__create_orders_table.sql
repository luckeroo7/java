CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(15,2) NOT NULL,
    delivery_address TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Создаём индекс для быстрого поиска по customer_id
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);

-- Создаём индекс для сортировки по дате
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at);

-- Комментарии к таблице (для документации)
COMMENT ON TABLE orders IS 'Таблица заказов';
COMMENT ON COLUMN orders.id IS 'Уникальный идентификатор заказа';
COMMENT ON COLUMN orders.customer_id IS 'ID клиента';
COMMENT ON COLUMN orders.status IS 'Статус заказа: PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED';
COMMENT ON COLUMN orders.total_amount IS 'Общая сумма заказа';
COMMENT ON COLUMN orders.delivery_address IS 'Адрес доставки';
COMMENT ON COLUMN orders.created_at IS 'Дата и время создания заказа';