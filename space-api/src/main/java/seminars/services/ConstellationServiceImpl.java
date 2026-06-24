package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.satellites.Satellite;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.dto.ConstellationStatusResponse;
import seminars.dto.SatelliteStatusResponse;
import seminars.exceptions.DuplicateResourceException;
import seminars.exceptions.ResourceNotFoundException;
import seminars.exceptions.SpaceOperationException;
import seminars.repository.ConstellationRepository;
import seminars.repository.SatelliteRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConstellationServiceImpl implements ConstellationService {
    private final ConstellationRepository constellationRepository;
    private final SatelliteRepository satelliteRepository;

    @Override
    public SatelliteConstellation createConstellation(String constellationName) {
        validateName(constellationName);
        if (constellationRepository.existsByName(constellationName)) {
            throw new DuplicateResourceException("Группировка с именем '" + constellationName + "' уже существует");
        }
        SatelliteConstellation constellation = new SatelliteConstellation(constellationName);
        log.info("Создана спутниковая группировка: {}", constellationName);

        return constellationRepository.save(constellation);
    }

    @Override
    @Cacheable(value = "constellation", key = "#constellationName")
    @Transactional(readOnly = true)
    public SatelliteConstellation getConstellationByName(String constellationName) {
        validateName(constellationName);

        return constellationRepository.findByName(constellationName)
                .orElseThrow(() -> new ResourceNotFoundException("Группировка не найдена по имени: "
                        + constellationName));
    }

    @Transactional(readOnly = true)
    @Override
    public SatelliteConstellation getConstellationById(Long id) {
        return constellationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Группировка не найдена по id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<SatelliteConstellation> getAllConstellations() {
        return constellationRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<SatelliteConstellation> getAllConstellationsWithSatellites() {
        return constellationRepository.findAll();
    }

    @Override
    public void deleteConstellation(String name) {
        if (!constellationRepository.existsByName(name)) {
            throw new ResourceNotFoundException("Группировка с именем '" + name + "' не найдена");
        }
        constellationRepository.deleteByName(name);
        log.info("Удалена группировка с именем: {}", name);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "constellation", key = "#constellationId"),
        @CacheEvict(value = "satellites", key = "'all'")
    })
    public void addSatelliteToConstellation(Long constellationId, Long satelliteId) {
        SatelliteConstellation constellation = constellationRepository.findById(constellationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Не существует группировки c id = " + constellationId));
        Satellite satellite = satelliteRepository.findById(satelliteId)
                .orElseThrow(() -> new ResourceNotFoundException("Не существует спутника c id = " + satelliteId));

        constellation.addSatellite(satellite);
        satellite.setConstellation(constellation);
        satelliteRepository.save(satellite);
        log.info("{} добавлен в группировку \"{}\"", satellite.getName(), constellation.getName());
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "constellation", key = "#constellationName"),
        @CacheEvict(value = "satellites", key = "'all'")
    })
    public void addSatelliteToConstellation(String constellationName, String satelliteName) {
        validateName(constellationName);
        SatelliteConstellation constellation = getConstellationByName(constellationName);
        Satellite satellite = satelliteRepository.findByName(satelliteName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Спутник не найден по имени: " + satelliteName));

        constellation.addSatellite(satellite);
        satellite.setConstellation(constellation);
        satelliteRepository.save(satellite);
        log.info("{} добавлен в группировку \"{}\"", satellite.getName(), constellation.getName());
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "constellation", key = "#constellationName"),
        @CacheEvict(value = "satellites", key = "'all'")
    })
    public void removeSatelliteFromConstellation(String constellationName, String satelliteName) {
        validateName(constellationName);
        SatelliteConstellation constellation = getConstellationByName(constellationName);
        Satellite satellite = constellation.getSatellites().stream()
                .filter(sat -> sat.getName().equals(satelliteName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Спутник не найден"));
        constellation.removeSatellite(satellite);
        satelliteRepository.delete(satellite);
        log.info("{} удален из группировки \"{}\"", satellite.getName(), constellation.getName());
    }

    @Override
    public ConstellationStatusResponse executeConstellationMission(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellationByName(constellationName);
        log.info("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        constellation.executeAllMissions();
        return getConstellationStatus(constellationName);
    }

    @Override
    public ConstellationStatusResponse activateAllSatellites(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellationByName(constellationName);
        log.info("АКТИВАЦИЯ СПУТНИКОВ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        for (Satellite satellite : constellation.getSatellites()) {
            boolean activated = satellite.activate();
            if (activated) {
                log.info("Спутник {} активирован", satellite.getName());
            } else {
                log.warn("Спутник {} не удалось активировать (недостаточно энергии)", satellite.getName());
            }
        }
        return getConstellationStatus(constellationName);
    }

    @Override
    public void showConstellationStatus(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation satelliteConstellation = getConstellationByName(constellationName);
        log.info("{}", satelliteConstellation.getAllSatellitesStatuses());
    }

    @Override
    public ConstellationStatusResponse getConstellationStatus(String constellationName) {
        validateName(constellationName);
        SatelliteConstellation constellation = getConstellationByName(constellationName);

        List<SatelliteStatusResponse> satelliteStatuses = constellation.getSatellites().stream()
                .map(sat -> new SatelliteStatusResponse(
                        sat.getId(),
                        sat.getName(),
                        sat.getClass().getSimpleName(),
                        sat.getState().isActive(),
                        sat.getEnergy().getBatteryLevel()
                ))
                .toList();

        long activeCount = satelliteStatuses.stream().filter(SatelliteStatusResponse::active).count();

        return new ConstellationStatusResponse(
                constellation.getId(),
                constellation.getName(),
                constellation.getSatellites().size(),
                (int) activeCount,
                satelliteStatuses
        );
    }

    @Override
    public void renameConstellation(String oldName, String newName) {
        validateName(oldName);
        validateName(newName);
        SatelliteConstellation constellation = getConstellationByName(oldName);
        if (!oldName.equals(newName) && constellationRepository.existsByName(newName)) {
            throw new DuplicateResourceException("Группировка с именем '" + newName + "' уже существует");
        }
        constellation.setName(newName);
        constellationRepository.save(constellation);
        log.info("Группировка переименована с '{}' на '{}'", oldName, newName);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }
    }
}
