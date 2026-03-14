package seminars.domains.satellites;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EnergySystem {
    protected double batteryLevel;
    private double lowBatteryThreshold;
    private double maxBattery;
    private double minBattery;

    public boolean consume(double amount) {
        if (amount <= 0) {
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

    public int batteryLevelToPercent() {
        return (int)(batteryLevel * 100);
    }

}
