CREATE TABLE IF NOT EXISTS  inventory_items (
    inventory_item_id VARCHAR(64) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    reserved_quantity INT NOT NULL DEFAULT 0,
    quantity INT NOT NULL,
    price INT NOT NULL
);
