package seminars.services;

import seminars.domains.satellites.EnergySystem;

public interface EnergySystemService {
    EnergySystem createEnergySystem(double batteryLevel);
    EnergySystem getEnergySystemById(Long id);
    boolean consumeEnergy(Long energySystemId, double amount);
    boolean hasSufficientPower(Long energySystemId);
}
