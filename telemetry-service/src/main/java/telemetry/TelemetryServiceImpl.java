package telemetry;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.telemetry.proto.MetricValue;
import org.example.telemetry.proto.TelemetryRequest;
import org.example.telemetry.proto.TelemetryServiceGrpc;
import org.example.telemetry.proto.TelemetryUpdate;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GrpcService
public class TelemetryServiceImpl extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    boolean isShutdown() {
        return executor.isShutdown();
    }

    private static final List<EmulatedSatellite> SATELLITES = List.of(
            new EmulatedSatellite("emulated-satellite-1", "Космос-1"),
            new EmulatedSatellite("emulated-satellite-2", "Космос-2"),
            new EmulatedSatellite("emulated-satellite-3", "Космос-3")
    );

    @Override
    public void streamTelemetry(TelemetryRequest request,
                                StreamObserver<TelemetryUpdate> responseObserver) {
        executor.scheduleAtFixedRate(() -> {
            for (EmulatedSatellite satellite : SATELLITES) {
                satellite.updateTemperatures();

                TelemetryUpdate update = TelemetryUpdate.newBuilder()
                        .setDeviceId(satellite.getId())
                        .setTimestamp(System.currentTimeMillis())
                        .putMetrics("satellite_name", MetricValue.newBuilder()
                                .setStringValue(satellite.getName()).build())
                        .putMetrics("cpu_temperature", MetricValue.newBuilder()
                                .setDoubleValue(satellite.getInternalTemp()).build())
                        .putMetrics("external_temperature", MetricValue.newBuilder()
                                .setDoubleValue(satellite.getExternalTemp()).build())
                        .build();

                responseObserver.onNext(update);
            }
        }, 0, 2000, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    public void shutdown() {
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

    private static class EmulatedSatellite {
        private final String id;
        private final String name;
        private double internalTemp;
        private double externalTemp;

        EmulatedSatellite(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getInternalTemp() {
            return internalTemp;
        }

        public double getExternalTemp() {
            return externalTemp;
        }

        public void updateTemperatures() {
            this.internalTemp = Math.random() * 30 + 15;
            this.externalTemp = Math.random() * 50 - 40;
        }
    }
}
