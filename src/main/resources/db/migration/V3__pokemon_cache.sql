-- Table for pokemon history
CREATE TABLE pokemon (
    pokemon_id BIGINT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    height INTEGER,
    weight INTEGER,
    base_experience INTEGER,
    image_url VARCHAR(500),
    raw_json TEXT NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) DEFAULT 'system',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) DEFAULT 'system',
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(255)
);