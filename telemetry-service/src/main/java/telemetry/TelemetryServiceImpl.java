package telemetry;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.telemetry.proto.MetricValue;
import org.example.telemetry.proto.TelemetryRequest;
import org.example.telemetry.proto.TelemetryServiceGrpc;
import org.example.telemetry.proto.TelemetryUpdate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GrpcService
public class TelemetryServiceImpl extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final Map<String, StreamObserver<TelemetryUpdate>> activeStreams = new ConcurrentHashMap<>();

    @Override
    public void streamTelemetry(TelemetryRequest request,
                                StreamObserver<TelemetryUpdate> responseObserver) {
        String deviceId = request.getDeviceId();
        int intervalMs = request.getIntervalMs();

        activeStreams.put(deviceId, responseObserver);

        executor.scheduleAtFixedRate(() -> {
            StreamObserver<TelemetryUpdate> observer = activeStreams.get(deviceId);
            if (observer != null) {
                double temp = Math.random() * 30 + 15;
                double externalTemp = Math.random() * 50 - 40;

                TelemetryUpdate update = TelemetryUpdate.newBuilder()
                        .setDeviceId(deviceId)
                        .setTimestamp(System.currentTimeMillis())
                        .putMetrics("cpu_temperature", MetricValue.newBuilder()
                                .setDoubleValue(temp).build())
                        .putMetrics("external_temperature", MetricValue.newBuilder()
                                .setDoubleValue(externalTemp).build())
                        .build();

                observer.onNext(update);
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
        activeStreams.clear();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}