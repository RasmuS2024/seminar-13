package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.constants.EnergySystemConstants;
import seminars.domains.satellites.EnergySystem;
import seminars.exceptions.ResourceNotFoundException;
import seminars.exceptions.SpaceOperationException;
import seminars.repository.EnergySystemRepository;

import java.util.List;

import static seminars.constants.EnergySystemConstants.MAX_BATTERY;
import static seminars.constants.EnergySystemConstants.MIN_BATTERY;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EnergySystemServiceImpl implements EnergySystemService {
    private final EnergySystemRepository energySystemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EnergySystem> getAllEnergySystems() {
        return energySystemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public EnergySystem getEnergySystemById(Long id) {
        return energySystemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Энергосистема не найдена по id: " + id));
    }

    @Override
    public EnergySystem updateEnergySystem(Long id, double batteryLevel) {
        validateBatteryLevel(batteryLevel);

        EnergySystem energySystem = getEnergySystemById(id);
        energySystem.setBatteryLevel(batteryLevel);

        log.info("Обновлена энергосистема id = {}, новый заряд: {}%", id, (int)(batteryLevel * 100));
        return energySystem;
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

    @Override
    public boolean consumeEnergy(Long energySystemId, double amount) {
        if (amount <= 0) {
            throw new SpaceOperationException("Потребляемая энергия должна быть положительной");
        }

        EnergySystem energySystem = getEnergySystemById(energySystemId);
        boolean result = energySystem.consume(amount);

        if (result) {
            log.info("Энергосистема id = {}: потреблено {} ед. энергии", energySystemId, amount);
        } else {
            log.warn("Энергосистема id = {}: недостаточно энергии для потребления {} ед.", energySystemId, amount);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasSufficientPower(Long energySystemId) {
        EnergySystem energySystem = getEnergySystemById(energySystemId);
        return energySystem.hasSufficientPower();
    }

    private void validateBatteryLevel(double batteryLevel) {
        if (batteryLevel < MIN_BATTERY || batteryLevel > MAX_BATTERY) {
            throw new SpaceOperationException("Уровень заряда должен быть от "
                    + MIN_BATTERY + " до " + MAX_BATTERY);
        }
    }
}
