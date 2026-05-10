package seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import seminars.domains.telemetry.TelemetryHistory;

import java.util.List;

@Repository
public interface TelemetryHistoryRepository extends JpaRepository<TelemetryHistory, Long> {
    List<TelemetryHistory> findBySatelliteIdOrderByTimestampDesc(Long satelliteId);
    List<TelemetryHistory> findTop100BySatelliteIdOrderByTimestampDesc(Long satelliteId);
}