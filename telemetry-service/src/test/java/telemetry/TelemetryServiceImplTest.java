package telemetry;

import io.grpc.stub.StreamObserver;
import org.example.telemetry.proto.MetricValue;
import org.example.telemetry.proto.TelemetryRequest;
import org.example.telemetry.proto.TelemetryUpdate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@DisplayName("Unit-тесты TelemetryServiceImpl")
class TelemetryServiceImplTest {

    private StreamObserver<TelemetryUpdate> responseObserver;
    private TelemetryServiceImpl telemetryService;
    private List<TelemetryUpdate> capturedUpdates;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        responseObserver = mock(StreamObserver.class);
        capturedUpdates = new CopyOnWriteArrayList<>();

        doAnswer(invocation -> {
            capturedUpdates.add(invocation.getArgument(0, TelemetryUpdate.class));
            return null;
        }).when(responseObserver).onNext(any(TelemetryUpdate.class));

        telemetryService = new TelemetryServiceImpl();
    }

    @AfterEach
    void tearDown() {
        telemetryService.shutdown();
    }

    @Test
    @DisplayName("streamTelemetry отправляет обновления для 3 спутников")
    void streamTelemetrySendsUpdatesForThreeSatellites() throws Exception {
        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId("test-device")
                .setIntervalMs(2000)
                .build();

        telemetryService.streamTelemetry(request, responseObserver);

        Thread.sleep(4500);

        assertTrue(capturedUpdates.size() >= 3,
                "Должно быть отправлено минимум 3 обновления, получено: " + capturedUpdates.size());
    }

    @Test
    @DisplayName("streamTelemetry отправляет обновления с правильными deviceId")
    void streamTelemetrySendsCorrectDeviceIds() throws Exception {
        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId("test-device")
                .setIntervalMs(2000)
                .build();

        telemetryService.streamTelemetry(request, responseObserver);

        Thread.sleep(4500);

        List<String> deviceIds = capturedUpdates.stream()
                .map(TelemetryUpdate::getDeviceId)
                .distinct()
                .toList();

        assertTrue(deviceIds.contains("emulated-satellite-1"), "Должен содержать satellite-1");
        assertTrue(deviceIds.contains("emulated-satellite-2"), "Должен содержать satellite-2");
        assertTrue(deviceIds.contains("emulated-satellite-3"), "Должен содержать satellite-3");
    }

    @Test
    @DisplayName("streamTelemetry включает все метрики")
    void streamTelemetryIncludesAllMetrics() throws Exception {
        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId("test-device")
                .setIntervalMs(2000)
                .build();

        telemetryService.streamTelemetry(request, responseObserver);

        Thread.sleep(4500);

        assertTrue(capturedUpdates.size() >= 3, "Должно быть минимум 3 обновления");

        for (TelemetryUpdate update : capturedUpdates) {
            assertTrue(update.getMetricsMap().containsKey("satellite_name"),
                    "Должна быть метрика satellite_name");
            assertTrue(update.getMetricsMap().containsKey("cpu_temperature"),
                    "Должна быть метрика cpu_temperature");
            assertTrue(update.getMetricsMap().containsKey("external_temperature"),
                    "Должна быть метрика external_temperature");
        }
    }

    @Test
    @DisplayName("streamTelemetry включает временную метку")
    void streamTelemetryIncludesTimestamp() throws Exception {
        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId("test-device")
                .setIntervalMs(2000)
                .build();

        telemetryService.streamTelemetry(request, responseObserver);

        Thread.sleep(2500);

        assertFalse(capturedUpdates.isEmpty(), "Должно быть получено хотя бы одно обновление");
        TelemetryUpdate update = capturedUpdates.get(0);

        assertTrue(update.getTimestamp() > 0, "Временная метка должна быть больше 0");
    }

    @Test
    @DisplayName("satellite_name имеет тип string")
    void satelliteNameIsStringType() throws Exception {
        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId("test-device")
                .setIntervalMs(2000)
                .build();

        telemetryService.streamTelemetry(request, responseObserver);

        Thread.sleep(2500);

        assertFalse(capturedUpdates.isEmpty(), "Должно быть получено хотя бы одно обновление");
        TelemetryUpdate update = capturedUpdates.get(0);

        MetricValue nameMetric = update.getMetricsMap().get("satellite_name");
        assertNotNull(nameMetric, "Метрика satellite_name не должна быть null");
        assertTrue(nameMetric.hasStringValue(), "satellite_name должен иметь строковое значение");
    }

    @Test
    @DisplayName("cpu_temperature в диапазоне 15-45")
    void cpuTemperatureInRange() throws Exception {
        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId("test-device")
                .setIntervalMs(2000)
                .build();

        telemetryService.streamTelemetry(request, responseObserver);

        Thread.sleep(4500);

        assertTrue(capturedUpdates.size() >= 3, "Должно быть минимум 3 обновления");

        for (TelemetryUpdate update : capturedUpdates) {
            MetricValue cpuMetric = update.getMetricsMap().get("cpu_temperature");
            assertNotNull(cpuMetric, "Метрика cpu_temperature не должна быть null");
            assertTrue(cpuMetric.hasDoubleValue(), "cpu_temperature должен иметь числовое значение");

            double cpuTemp = cpuMetric.getDoubleValue();
            assertTrue(cpuTemp >= 15 && cpuTemp <= 45,
                    "cpu_temperature должен быть в диапазоне 15-45, получено: " + cpuTemp);
        }
    }

    @Test
    @DisplayName("external_temperature в диапазоне -40 до +10")
    void externalTemperatureInRange() throws Exception {
        TelemetryRequest request = TelemetryRequest.newBuilder()
                .setDeviceId("test-device")
                .setIntervalMs(2000)
                .build();

        telemetryService.streamTelemetry(request, responseObserver);

        Thread.sleep(4500);

        assertTrue(capturedUpdates.size() >= 3, "Должно быть минимум 3 обновления");

        for (TelemetryUpdate update : capturedUpdates) {
            MetricValue externalMetric = update.getMetricsMap().get("external_temperature");
            assertNotNull(externalMetric, "Метрика external_temperature не должна быть null");
            assertTrue(externalMetric.hasDoubleValue(), "external_temperature должен иметь числовое значение");

            double externalTemp = externalMetric.getDoubleValue();
            assertTrue(externalTemp >= -40 && externalTemp <= 10,
                    "external_temperature должен быть в диапазоне -40...+10, получено: " + externalTemp);
        }
    }

    @Test
    @DisplayName("shutdown корректно завершает executor")
    void shutdownTerminatesExecutor() {
        telemetryService.shutdown();

        assertTrue(telemetryService.isShutdown(), "Executor должен быть завершен");
    }
}
