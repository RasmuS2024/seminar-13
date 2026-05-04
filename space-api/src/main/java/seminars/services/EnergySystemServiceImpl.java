package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.constants.EnergySystemConstants;
import seminars.domains.satellites.EnergySystem;
import seminars.exceptions.SpaceOperationException;
import seminars.repository.EnergySystemRepository;

import static seminars.constants.EnergySystemConstants.MAX_BATTERY;
import static seminars.constants.EnergySystemConstants.MIN_BATTERY;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EnergySystemServiceImpl implements EnergySystemService {
    private final EnergySystemRepository energySystemRepository;
    private EnergySystemServiceImpl self;

    @Autowired
    public void setSelf(@Lazy EnergySystemServiceImpl self) {
        this.self = self;
    }

    @Override
    public EnergySystem createEnergySystem(double batteryLevel) {
        validateBatteryLevel(batteryLevel);

        EnergySystem energySystem = EnergySystem.builder()
                .batteryLevel(batteryLevel)
                .lowBatteryThreshold(EnergySystemConstants.LOW_BATTERY_THRESHOLD)
                .maxBattery(MAX_BATTERY)
                .minBattery(MIN_BATTERY)
                .build();

        log.info("Создана энергосистема (заряд: {}%)", (int) (batteryLevel * 100));

        return energySystemRepository.save(energySystem);
    }

    @Transactional(readOnly = true)
    @Override
    public EnergySystem getEnergySystemById(Long id) {
        return energySystemRepository.findById(id)
                .orElseThrow(() -> new SpaceOperationException("Энергосистема не найдена по id: " + id));
    }

    @Override
    public boolean consumeEnergy(Long energySystemId, double amount) {
        if (amount <= 0) {
            throw new SpaceOperationException("Потребляемая энергия должна быть положительной");
        }

        EnergySystem energySystem = self.getEnergySystemById(energySystemId);
        boolean result = energySystem.consume(amount);

        if (result) {
            log.info("Энергосистема id = {}: потреблено {} ед. энергии", energySystemId, amount);
        } else {
            log.warn("Энергосистема id = {}: недостаточно энергии для потребления {} ед.", energySystemId, amount);
        }

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasSufficientPower(Long energySystemId) {
        EnergySystem energySystem = self.getEnergySystemById(energySystemId);
        return energySystem.hasSufficientPower();
    }

    private void validateBatteryLevel(double batteryLevel) {
        if (batteryLevel < MIN_BATTERY || batteryLevel > MAX_BATTERY) {
            throw new SpaceOperationException("Уровень заряда должен быть от "
                    + MIN_BATTERY + " до " + MAX_BATTERY);
        }
    }
}
