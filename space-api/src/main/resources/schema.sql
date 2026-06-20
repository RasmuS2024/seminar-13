-- satellite_constellation
CREATE TABLE IF NOT EXISTS satellite_constellation (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- energy_system
CREATE TABLE IF NOT EXISTS energy_system (
    id BIGSERIAL PRIMARY KEY,
    battery_level DOUBLE PRECISION NOT NULL,
    low_battery_threshold DOUBLE PRECISION NOT NULL,
    max_battery DOUBLE PRECISION NOT NULL,
    min_battery DOUBLE PRECISION NOT NULL
);

-- satellite
CREATE TABLE IF NOT EXISTS satellite (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    constellation_id BIGINT REFERENCES satellite_constellation(id),
    energy_id BIGINT UNIQUE REFERENCES energy_system(id),
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    status_message VARCHAR(255),
    satellite_type VARCHAR(50)
);

CREATE INDEX IF NOT EXISTS idx_satellite_constellation_id ON satellite(constellation_id);
CREATE INDEX IF NOT EXISTS idx_satellite_is_active ON satellite(is_active);
CREATE INDEX IF NOT EXISTS idx_satellite_type ON satellite(satellite_type);
CREATE INDEX IF NOT EXISTS idx_satellite_name ON satellite(name);

-- imaging_satellite
CREATE TABLE IF NOT EXISTS imaging_satellite (
    satellite_id BIGINT PRIMARY KEY REFERENCES satellite(id) ON DELETE CASCADE,
    resolution DOUBLE PRECISION NOT NULL,
    photos_taken INTEGER NOT NULL DEFAULT 0
);

-- communication_satellite
CREATE TABLE IF NOT EXISTS communication_satellite (
    satellite_id BIGINT PRIMARY KEY REFERENCES satellite(id) ON DELETE CASCADE,
    bandwidth DOUBLE PRECISION NOT NULL
);

-- telemetry_history
CREATE TABLE IF NOT EXISTS telemetry_history (
    id BIGSERIAL PRIMARY KEY,
    satellite_id BIGINT REFERENCES satellite(id),
    cpu_temperature DOUBLE PRECISION,
    external_temperature DOUBLE PRECISION,
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_telemetry_satellite_id ON telemetry_history(satellite_id);
CREATE INDEX IF NOT EXISTS idx_telemetry_timestamp ON telemetry_history(timestamp);

-- outbox
CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(10) NOT NULL,
    CONSTRAINT chk_outbox_event_type CHECK (event_type IN ('CREATED', 'DELETED')),
    payload TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

CREATE INDEX IF NOT EXISTS idx_outbox_status_created_at ON outbox (status, created_at);
CREATE INDEX IF NOT EXISTS idx_outbox_aggregate_id ON outbox (aggregate_id);
