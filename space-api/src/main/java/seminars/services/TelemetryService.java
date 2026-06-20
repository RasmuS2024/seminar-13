package seminars.services;

public interface TelemetryService {
    void startMonitoring(Long satelliteId);

    void stopMonitoring(Long satelliteId);
    boolean isMonitoring(Long satelliteId);
}
