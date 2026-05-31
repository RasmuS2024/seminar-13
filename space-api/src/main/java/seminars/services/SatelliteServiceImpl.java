package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.dto.SatelliteStatusResponse;
import seminars.exceptions.DuplicateResourceException;
import seminars.exceptions.ResourceNotFoundException;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.SatelliteFactory;
import seminars.kafka.KafkaService;
import seminars.kafka.KafkaUtils;
import seminars.kafka.SatelliteEvent;
import seminars.repository.SatelliteRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SatelliteServiceImpl implements SatelliteService {
    private final List<SatelliteFactory> factories;
    private final SatelliteRepository satelliteRepository;
    private final TelemetryService telemetryService;
    private final KafkaService kafkaService;
    private static final String SATELLITE_EVENTS_TOPIC = "satellite-events";

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

        if (satelliteRepository.findByName(param.getName()).isPresent()) {
            throw new DuplicateResourceException("Спутник с именем '" + param.getName() + "' уже существует");
        }

        Satellite satellite = factory.createSatelliteWithParameter(param);

        log.info("Создан спутник: {} (заряд: {}%)",
                satellite.getName(),
                (int) (satellite.getEnergy().getBatteryLevel() * 100));

        Satellite saved = satelliteRepository.save(satellite);

        kafkaService.sendToKafkaSatellite(
                SATELLITE_EVENTS_TOPIC,
                KafkaUtils.createEvent(saved, SatelliteEvent.EventType.CREATED)
        );

        return saved;
    }

    @Transactional(readOnly = true)
    @Override
    public Satellite getSatelliteById(Long id) {
        return satelliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Спутник не найден по id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public Satellite getSatelliteByName(String name) {
        if (name == null || name.isBlank()) {
            throw new SpaceOperationException("Имя спутника не может быть пустым");
        }
        return satelliteRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Спутник не найден по имени: " + name));
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
        Satellite satellite = satelliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Спутник с id = " + id + " не найден"));

        satelliteRepository.deleteById(id);

        kafkaService.sendToKafkaSatellite(
                SATELLITE_EVENTS_TOPIC,
                KafkaUtils.createEvent(satellite, SatelliteEvent.EventType.DELETED)
        );

        telemetryService.stopMonitoring(id);

        log.info("Удален спутник с id: {}", id);
    }

    @Override
    public SatelliteStatusResponse activateSatellite(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        boolean activated = satellite.activate();

        if (activated) {
            String deviceId = "satellite-" + satelliteId;
            telemetryService.startMonitoring(satelliteId, deviceId);
            log.info("Спутник {} активирован", satellite.getName());
        } else {
            log.warn("Спутник {} не удалось активировать (недостаточно энергии)", satellite.getName());
        }

        return getSatelliteStatus(satelliteId);
    }

    @Override
    public SatelliteStatusResponse deActivateSatellite(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        satellite.deActivate();
        telemetryService.stopMonitoring(satelliteId);
        log.info("Спутник {} деактивирован", satellite.getName());
        return getSatelliteStatus(satelliteId);
    }

    @Override
    public SatelliteStatusResponse performSatelliteMission(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        log.info("ВЫПОЛНЕНИЕ МИССИИ СПУТНИКА {}", satellite.getName().toUpperCase());
        satellite.performMission();
        return getSatelliteStatus(satelliteId);
    }

    @Override
    public SatelliteStatusResponse getSatelliteStatus(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        log.info("Статус спутника {}: active={}", satellite.getName(), satellite.getState().isActive());
        return new SatelliteStatusResponse(
                satellite.getId(),
                satellite.getName(),
                satellite.getClass().getSimpleName(),
                satellite.getState().isActive(),
                satellite.getEnergy().getBatteryLevel()
        );
    }

    @Override
    public Satellite updateSatellite(Long id, SatelliteParam param) {
        Satellite satellite = getSatelliteById(id);

        if (!satellite.getName().equals(param.getName())) {
            satelliteRepository.findByName(param.getName())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException("Имя '" + param.getName() + "' уже используется другим спутником");
                    });
        }

        satellite.setName(param.getName());
        log.info("Обновлен спутник с id: {}", id);
        return satelliteRepository.save(satellite);
    }
}
