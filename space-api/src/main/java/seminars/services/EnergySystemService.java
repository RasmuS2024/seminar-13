package seminars.services;

import seminars.domains.satellites.EnergySystem;

import java.util.List;

public interface EnergySystemService {
    List<EnergySystem> getAllEnergySystems();
    EnergySystem getEnergySystemById(Long id);
    EnergySystem updateEnergySystem(Long id, double batteryLevel);
    EnergySystem createEnergySystem(double batteryLevel);
    boolean consumeEnergy(Long energySystemId, double amount);
    boolean hasSufficientPower(Long energySystemId);
}
