package seminars.domains.telemetry;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "satellite_id")
    private Long satelliteId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "cpu_temperature")
    private Double cpuTemperature;

    @Column(name = "external_temperature")
    private Double externalTemperature;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}