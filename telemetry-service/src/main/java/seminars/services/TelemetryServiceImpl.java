package seminars.services;

import io.grpc.stub.StreamObserver;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.telemetry.proto.TelemetryRequest;
import org.example.telemetry.proto.TelemetryServiceGrpc;
import org.example.telemetry.proto.TelemetryUpdate;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@GrpcService
@RequiredArgsConstructor
public class TelemetryServiceImpl extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private final Random random = new Random();
    private final SatelliteIdRepository satelliteIdRepository;

    @Override
    public void streamTelemetry(TelemetryRequest request,
                                StreamObserver<TelemetryUpdate> responseObserver) {
        long satelliteId = request.getSatelliteId();
        satelliteIdRepository.add(satelliteId);

        executor.scheduleAtFixedRate(() -> {
            try {
                TelemetryUpdate update = TelemetryUpdate.newBuilder()
                        .setSatelliteId(satelliteId)
                        .setTemperatureInside(20.0 + random.nextDouble() * 10)
                        .setTemperatureOutside(-50.0 + random.nextDouble() * 30)
                        .setTimestamp(System.currentTimeMillis())
                        .build();
                responseObserver.onNext(update);
            } catch (Exception e) {
                responseObserver.onError(e);
            }
        }, 0, request.getIntervalMs(), TimeUnit.MILLISECONDS);
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

}
