package seminars.domains.satellites;

import lombok.Data;
import seminars.constants.EnergySystemConstants;

@Data
public abstract class Satellite {
    protected String name;
    protected SatelliteState state;
    protected EnergySystem energy;

    protected Satellite(String name, double batteryLevel) {
        this.name = name;
        this.state = new SatelliteState();
        this.energy = EnergySystem.builder()
                .batteryLevel(batteryLevel)
                .lowBatteryThreshold(EnergySystemConstants.LOW_BATTERY_THRESHOLD)
                .maxBattery(EnergySystemConstants.MAX_BATTERY)
                .minBattery(EnergySystemConstants.MIN_BATTERY)
                .build();

        System.out.println("Создан спутник: " + name + " (заряд: " + energy.batteryLevelToPercent() + "%)");
    }

    public boolean activate() {
        if (state.activate(energy.hasSufficientPower())) {
            System.out.println(name + ": Активация успешна");
            return true;
        }

        System.out.println(name + ": Ошибка активации (заряд: " + (getEnergy().batteryLevelToPercent() + "%)"));
        return false;
    }

    public void deActivate() {
        if (state.isActive()) {
            state.deActivate();
        }

        System.out.println(name + ": Деактивирован");
    }

    public abstract void performMission();

}

