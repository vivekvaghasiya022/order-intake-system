-- ========== NOTIFICATION SERVICE SCHEMA ==========

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    type BIGINT NOT NULL,
    delivered BOOLEAN NOT NULL,
    message VARCHAR(500) NULL,
    event_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Create index on order_id for faster lookup
CREATE INDEX idx_notifications_order_id ON notifications(order_id);

-- Create index on event_id for faster lookup
CREATE INDEX idx_notifications_event_id ON notifications(event_id);
