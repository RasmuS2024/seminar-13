CREATE TABLE IF NOT EXISTS inbox_event (
    event_id UUID PRIMARY KEY,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(10) NOT NULL
        CONSTRAINT chk_inbox_event_type CHECK (event_type IN ('CREATED', 'DELETED')),
    processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_inbox_event_aggregate_id ON inbox_event(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_inbox_event_event_type ON inbox_event(event_type);

CREATE TABLE IF NOT EXISTS active_satellite (
    satellite_id BIGINT PRIMARY KEY
);
