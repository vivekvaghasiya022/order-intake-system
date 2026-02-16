-- ========== ORDER SERVICE SCHEMA ==========

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_email VARCHAR(255) NOT NULL,
    product_code VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_quantity_positive CHECK (quantity > 0)
    );

CREATE TABLE IF NOT EXISTS outbox_event (
    outbox_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    event_type VARCHAR(100),
    payload JSON NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    processed_at TIMESTAMP,
    retry_count INT DEFAULT 0,
    last_attempt_at TIMESTAMP
);

-- Create index on status for faster lookup
CREATE INDEX idx_outbox_status ON outbox_event(status);
