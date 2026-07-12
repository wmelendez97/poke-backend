-- Table for global and user-specific search history
CREATE TABLE search_history (
    id BIGSERIAL PRIMARY KEY,
    user_identifier VARCHAR(255),
    search_type VARCHAR(100) NOT NULL,
    query_value VARCHAR(500),
    endpoint VARCHAR(255),
    success BOOLEAN NOT NULL,
    status_code INTEGER,
    error_message TEXT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255)
);
