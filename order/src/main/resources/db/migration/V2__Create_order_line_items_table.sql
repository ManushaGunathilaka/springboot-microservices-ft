CREATE TABLE t_order_line_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_code VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    quantity INT NOT NULL,
    order_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES t_orders(id) ON DELETE CASCADE
);
