package seminars.services;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.telemetry.proto.MetricValue;
import org.example.telemetry.proto.TelemetryRequest;
import org.example.telemetry.proto.TelemetryServiceGrpc;
import org.example.telemetry.proto.TelemetryUpdate;
import org.springframework.stereotype.Service;
import seminars.domains.telemetry.TelemetryHistory;
import seminars.repository.TelemetryHistoryRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryHistoryRepository telemetryHistoryRepository;

    @GrpcClient("telemetry-service")
    private TelemetryServiceGrpc.TelemetryServiceStub telemetryStub;

    private final Map<Long, StreamObserver<TelemetryUpdate>> activeObservers = new ConcurrentHashMap<>();
    private final Map<Long, AtomicBoolean> monitoringFlags = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void startMonitoring(Long satelliteId, String deviceId) {
        if (monitoringFlags.containsKey(satelliteId) && monitoringFlags.get(satelliteId).get()) {
            log.warn("Telemetry monitoring already active for satellite {}", satelliteId);
            return;
        }

        AtomicBoolean isActive = new AtomicBoolean(true);
        monitoringFlags.put(satelliteId, isActive);

        StreamObserver<TelemetryUpdate> observer = new StreamObserver<>() {
            @Override
            public void onNext(TelemetryUpdate update) {
                if (!isActive.get()) {
                    return;
                }
                processUpdate(satelliteId, deviceId, update);
            }

            @Override
            public void onError(Throwable t) {
                log.error("gRPC error for satellite {}: {}", satelliteId, t.getMessage());
                isActive.set(false);
                monitoringFlags.remove(satelliteId);
                activeObservers.remove(satelliteId);
            }

            @Override
            public void onCompleted() {
                log.info("Telemetry stream completed for satellite {}", satelliteId);
                isActive.set(false);
                monitoringFlags.remove(satelliteId);
                activeObservers.remove(satelliteId);
            }
        };

        activeObservers.put(satelliteId, observer);

        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId(deviceId)
                .setIntervalMs(2000)
                .build();

        telemetryStub.streamTelemetry(request, observer);
        log.info("Started telemetry monitoring for satellite {} with device {}", satelliteId, deviceId);
    }

    @Override
    public void stopMonitoring(Long satelliteId) {
        AtomicBoolean isActive = monitoringFlags.get(satelliteId);
        if (isActive != null) {
            isActive.set(false);
        }

        StreamObserver<TelemetryUpdate> observer = activeObservers.remove(satelliteId);
        if (observer != null) {
            log.info("Stopped telemetry monitoring for satellite {}", satelliteId);
        }

        monitoringFlags.remove(satelliteId);
    }

    @Override
    public boolean isMonitoring(Long satelliteId) {
        AtomicBoolean isActive = monitoringFlags.get(satelliteId);
        return isActive != null && isActive.get();
    }

    private void processUpdate(Long satelliteId, String deviceId, TelemetryUpdate update) {
        try {
            Double cpuTemp = null;
            Double externalTemp = null;

            MetricValue cpuMetric = update.getMetricsMap().get("cpu_temperature");
            if (cpuMetric != null && cpuMetric.hasDoubleValue()) {
                cpuTemp = cpuMetric.getDoubleValue();
            }

            MetricValue externalMetric = update.getMetricsMap().get("external_temperature");
            if (externalMetric != null && externalMetric.hasDoubleValue()) {
                externalTemp = externalMetric.getDoubleValue();
            }

            if (cpuTemp != null || externalTemp != null) {
                TelemetryHistory history = TelemetryHistory.builder()
                        .satelliteId(satelliteId)
                        .deviceId(deviceId)
                        .cpuTemperature(cpuTemp)
                        .externalTemperature(externalTemp)
                        .timestamp(update.getTimestamp())
                        .build();

                telemetryHistoryRepository.save(history);
                log.debug("Saved telemetry for satellite {}: cpu={}, external={}",
                        satelliteId, cpuTemp, externalTemp);
            }
        } catch (Exception e) {
            log.error("Failed to process telemetry update for satellite {}: {}", satelliteId, e.getMessage());
        }
    }
}
