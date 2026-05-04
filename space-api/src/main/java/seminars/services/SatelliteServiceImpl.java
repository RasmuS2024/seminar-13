package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.SatelliteParam;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.SatelliteFactory;
import seminars.repository.SatelliteRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SatelliteServiceImpl implements SatelliteService {
    private final List<SatelliteFactory> factories;
    private final SatelliteRepository satelliteRepository;

    @Override
    public Satellite createSatellite(SatelliteParam param) {
        if (param == null) {
            throw new SpaceOperationException("Параметры спутника не могут быть null");
        }
        if (param.getType() == null) {
            throw new SpaceOperationException("Тип спутника не может быть null");
        }

        SatelliteFactory factory = factories.stream()
                .filter(satelliteFactory -> satelliteFactory
                        .isSatelliteTypeSupported(param.getType()))
                .findFirst()
                .orElseThrow(() -> new SpaceOperationException("Данный тип параметров не поддерживается"));

        Satellite satellite = factory.createSatelliteWithParameter(param);

        log.info("Создан спутник: {} (заряд: {}%)",
                satellite.getName(),
                (int) (satellite.getEnergy().getBatteryLevel() * 100));

        return satelliteRepository.save(satellite);
    }

    @Transactional(readOnly = true)
    @Override
    public Satellite getSatelliteById(Long id) {
        return satelliteRepository.findById(id)
                .orElseThrow(() -> new SpaceOperationException("Спутник не найден по id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public Satellite getSatelliteByName(String name) {
        if (name == null || name.isBlank()) {
            throw new SpaceOperationException("Имя спутника не может быть пустым");
        }
        return satelliteRepository.findByName(name)
                .orElseThrow(() -> new SpaceOperationException("Спутник не найден по имени: " + name));
    }

    @Transactional(readOnly = true)
    @Override
    public List<Satellite> getAllSatellites() {
        return satelliteRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Satellite> getSatellitesByConstellationId(Long constellationId) {
        return satelliteRepository.findByConstellationId(constellationId);
    }

    @Override
    public void deleteSatellite(Long id) {
        if (!satelliteRepository.existsById(id)) {
            throw new SpaceOperationException("Спутник с id = " + id + " не найден");
        }
        satelliteRepository.deleteById(id);
        log.info("Удален спутник с id: {}", id);
    }

    @Override
    public void activateSatellite(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        boolean activated = satellite.activate();

        if (activated) {
            log.info("Спутник {} активирован", satellite.getName());
        } else {
            log.warn("Спутник {} не удалось активировать (недостаточно энергии)", satellite.getName());
        }
    }

    @Override
    public void deActivateSatellite(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        satellite.deActivate();
        log.info("Спутник {} деактивирован", satellite.getName());
    }

    @Override
    public void performSatelliteMission(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        log.info("ВЫПОЛНЕНИЕ МИССИИ СПУТНИКА {}", satellite.getName().toUpperCase());
        satellite.performMission();
    }

    @Override
    public String getSatelliteStatus(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        String status = satellite.getState().toString();
        log.info("{}", status);
        return status;
    }
}
