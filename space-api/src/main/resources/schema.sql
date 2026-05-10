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
    resolution DOUBLE PRECISION,
    photos_taken INTEGER,
    bandwidth DOUBLE PRECISION,
    satellite_type VARCHAR(50)
);

CREATE INDEX idx_satellite_constellation_id ON satellite(constellation_id);
CREATE INDEX idx_satellite_is_active ON satellite(is_active);
CREATE INDEX idx_satellite_type ON satellite(satellite_type);