package seminars.services;

public interface TelemetryService {
    void startMonitoring(Long satelliteId, String deviceId);

    void stopMonitoring(Long satelliteId);
    boolean isMonitoring(Long satelliteId);
}
