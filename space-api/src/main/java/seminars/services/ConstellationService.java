package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.satellites.Satellite;
import seminars.domains.constellations.SatelliteConstellation;
import seminars.exceptions.SpaceOperationException;
import seminars.repository.ConstellationRepository;
import seminars.repository.SatelliteRepository;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ConstellationService {
    private final ConstellationRepository constellationRepository;
    private final SatelliteRepository satelliteRepository;

    public SatelliteConstellation createConstellation(String constellationName) {
        validateName(constellationName);
        SatelliteConstellation constellation = new SatelliteConstellation(constellationName);
        log.info("Создана спутниковая группировка: {}", constellationName);

        return constellationRepository.save(constellation);
    }

    @Transactional(readOnly=true)
    public SatelliteConstellation getConstellationByName(String constellationName) {
        validateName(constellationName);

        return constellationRepository.findByConstellationName(constellationName)
                .orElseThrow(() -> new SpaceOperationException("Группировка не найдена по имени: " + constellationName));
    }

    @Transactional(readOnly=true)
    public SatelliteConstellation getConstellationById(Long id) {
        return constellationRepository.findById(id)
                .orElseThrow(() -> new SpaceOperationException("Группировка не найдена по id: " + id));
    }

    public void addSatelliteToConstellation(String constellationName, Satellite satellite) {
        validateName(constellationName);

        if (satellite == null) {
            throw new IllegalArgumentException("Спутник не может быть null");
        }

        SatelliteConstellation constellation = getConstellationByName(constellationName);
        constellation.addSatellite(satellite);
        log.info("{} добавлен в группировку \"{}\"", satellite.getName(), constellationName);
    }

    public void executeConstellationMission(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellationByName(constellationName);
        log.info("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        constellation.executeAllMissions();
    }

    public void activateAllSatellites(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellationByName(constellationName);
        log.info("АКТИВАЦИЯ СПУТНИКОВ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        constellation.activateAllSatellites();
    }

    public void showConstellationStatus(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation satelliteConstellation = getConstellationByName(constellationName);
        log.info(satelliteConstellation.getAllSatellitesStatuses());
    }

    public void updateConstellation(String name, SatelliteConstellation updatedConstellation) {
        validateName(name);
        constellationRepository.updateConstellation(name, updatedConstellation);
        log.info("Обновлена группировка: {}", name);
    }

    public void deleteConstellation(String name) {
        validateName(name);
        constellationRepository.deleteConstellation(name);
        log.info("Удалена группировка: {}", name);
    }

/**
 * Удаляет спутник из группировки.
 * @param constellationName имя группировки
 * @param satelliteName     имя спутника
 * @return true, если спутник был удалён
 */
    public boolean removeSatelliteFromConstellation(String constellationName, String satelliteName) {
        SatelliteConstellation constellation = constellationRepository.getConstellation(constellationName);
        if (constellation == null) {
            return false;
        }
        return constellation.getSatellites()
                .removeIf(sat -> sat.getName().equals(satelliteName));
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return constellationRepository.getAllConstellations();
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }
    }
}
