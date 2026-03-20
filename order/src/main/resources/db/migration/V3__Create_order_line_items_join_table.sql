CREATE TABLE t_orders_order_line_items_list (
    order_id BIGINT NOT NULL,
    order_line_items_list_id BIGINT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES t_orders(id) ON DELETE CASCADE,
    FOREIGN KEY (order_line_items_list_id) REFERENCES t_order_line_items(id) ON DELETE CASCADE,
    PRIMARY KEY (order_id, order_line_items_list_id)
);
