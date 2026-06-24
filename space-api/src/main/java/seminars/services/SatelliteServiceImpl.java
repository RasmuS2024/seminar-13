package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.satellites.Satellite;
import seminars.domains.satellites.params.SatelliteParam;
import seminars.dto.SatelliteStatusResponse;
import seminars.exceptions.DuplicateResourceException;
import seminars.exceptions.ResourceNotFoundException;
import seminars.exceptions.SpaceOperationException;
import seminars.factory.SatelliteFactory;
import seminars.kafka.KafkaUtils;
import seminars.kafka.SatelliteEvent;
import seminars.kafka.outbox.OutboxService;
import seminars.repository.SatelliteRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SatelliteServiceImpl implements SatelliteService {
    private final List<SatelliteFactory> factories;
    private final SatelliteRepository satelliteRepository;
    private final TelemetryService telemetryService;
    private final OutboxService outboxService;
    private static final String SATELLITE_EVENTS_TOPIC = "satellite-events";

    @Override
    @CacheEvict(value = "satellites", key = "'all'")
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

        Satellite saved = satelliteRepository.save(satellite);

        log.info("Создан спутник: id={}, name={}, type={}, battery={}%",
                saved.getId(),
                saved.getName(),
                saved.getClass().getSimpleName(),
                (int) (saved.getEnergy().getBatteryLevel() * 100));

        outboxService.publishToOutbox(
                saved.getId(),
                KafkaUtils.createEvent(saved, SatelliteEvent.EventType.CREATED)
        );

        return saved;
    }


    @Override
    @Cacheable(value = "satellite", key = "#id")
    @Transactional(readOnly = true)
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

    @Override
    @Cacheable(value = "satellites", key = "'all'")
    @Transactional(readOnly = true)
    public List<Satellite> getAllSatellites() {
        return satelliteRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Satellite> getSatellitesByConstellationId(Long constellationId) {
        return satelliteRepository.findByConstellationId(constellationId);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "satellite", key = "#id"),
        @CacheEvict(value = "satellites", key = "'all'")
    })
    public void deleteSatellite(Long id) {
        Satellite satellite = satelliteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Спутник с id = " + id + " не найден"));

        String name = satellite.getName();
        String type = satellite.getClass().getSimpleName();

        satelliteRepository.deleteById(id);

        telemetryService.stopMonitoring(id);

        outboxService.publishToOutbox(
                id,
                KafkaUtils.createEvent(satellite, SatelliteEvent.EventType.DELETED)
        );

        log.info("Удален спутник: id={}, name={}, type={}", id, name, type);
    }

    @Override
    public SatelliteStatusResponse activateSatellite(Long satelliteId) {
        Satellite satellite = getSatelliteById(satelliteId);
        boolean activated = satellite.activate();

        if (activated) {
            telemetryService.startMonitoring(satelliteId);
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
    @CacheEvict(value = "satellite", key = "#id")
    public Satellite updateSatellite(Long id, SatelliteParam param) {
        Satellite satellite = getSatelliteById(id);

        if (!satellite.getName().equals(param.getName())) {
            satelliteRepository.findByName(param.getName())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new DuplicateResourceException(
                                "Имя '" + param.getName() + "' уже используется другим спутником");
                    });
        }

        satellite.setName(param.getName());
        log.info("Обновлен спутник с id: {}", id);
        return satelliteRepository.save(satellite);
    }

    @Override
    @Cacheable(value = "satellite", key = "#constellationName + '::' + #satelliteName")
    @Transactional(readOnly = true)
    public Optional<Satellite> findByName(String constellationName, String satelliteName) {
        return satelliteRepository.findByNameAndConstellationName(satelliteName, constellationName);
    }
}
