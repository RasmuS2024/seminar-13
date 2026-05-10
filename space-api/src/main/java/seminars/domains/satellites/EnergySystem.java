package seminars.domains.satellites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "energy_system")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EnergySystem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "battery_level")
    protected double batteryLevel;

    @Column(nullable = false, name = "low_battery_threshold")
    private double lowBatteryThreshold;

    @Column(nullable = false, name = "max_battery")
    private double maxBattery;

    @Column(nullable = false, name = "min_battery")
    private double minBattery;

    public boolean consume(double amount) {
        if (amount <= 0) {
            return false;
        }

        if (batteryLevel - amount < minBattery) {
            return false;
        }

        if (batteryLevel - amount < lowBatteryThreshold) {
            return false;
        }

        batteryLevel -= amount;
        return true;
    }

    public boolean hasSufficientPower() {
        return batteryLevel > lowBatteryThreshold;
    }

}
