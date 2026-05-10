-- satellite_constellation
CREATE TABLE satellite_constellation (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- energy_system
CREATE TABLE energy_system (
    id BIGSERIAL PRIMARY KEY,
    battery_level DOUBLE PRECISION NOT NULL,
    low_battery_threshold DOUBLE PRECISION NOT NULL,
    max_battery DOUBLE PRECISION NOT NULL,
    min_battery DOUBLE PRECISION NOT NULL
);

-- satellite
CREATE TABLE satellite (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    constellation_id BIGINT REFERENCES satellite_constellation(id),
    state VARCHAR(50),
    energy_id BIGINT UNIQUE REFERENCES energy_system(id),
    satellite_type VARCHAR(50)
);

CREATE INDEX idx_satellite_constellation_id ON satellite(constellation_id);
CREATE INDEX idx_satellite_state ON satellite(state);
CREATE INDEX idx_satellite_type ON satellite(satellite_type);