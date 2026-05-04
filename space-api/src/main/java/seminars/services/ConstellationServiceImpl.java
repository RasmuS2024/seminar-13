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
            throw new SpaceOperationException("Группировка с именем '" + constellationName + "' уже существует");
        }
        SatelliteConstellation constellation = new SatelliteConstellation(constellationName);
        log.info("Создана спутниковая группировка: {}", constellationName);

        return constellationRepository.save(constellation);
    }

    @Transactional(readOnly = true)
    @Override
    public SatelliteConstellation getConstellationByName(String constellationName) {
        validateName(constellationName);

        return constellationRepository.findByName(constellationName)
                .orElseThrow(() -> new SpaceOperationException("Группировка не найдена по имени: "
                        + constellationName));
    }

    @Transactional(readOnly = true)
    @Override
    public SatelliteConstellation getConstellationById(Long id) {
        return constellationRepository.findById(id)
                .orElseThrow(() -> new SpaceOperationException("Группировка не найдена по id: " + id));
    }

    @Transactional(readOnly = true)
    @Override
    public List<SatelliteConstellation> getAllConstellations() {
        return constellationRepository.findAll();
    }

    @Override
    public List<SatelliteConstellation> getAllConstellationsWithSatellites() {
        return constellationRepository.findAllWithSatellites();
    }

    @Override
    public void deleteConstellation(Long id) {
        if (!constellationRepository.existsById(id)) {
            throw new SpaceOperationException("Группировка с id = " + id + " не найдена");
        }
        constellationRepository.deleteById(id);
        log.info("Удалена группировка с id: {}", id);
    }

    @Override
    public void deleteConstellationByName(String name) {
        if (!constellationRepository.existsByName(name)) {
            throw new SpaceOperationException("Группировка с именем '" + name + "' не найдена");
        }
        constellationRepository.deleteByName(name);
        log.info("Удалена группировка с именем: {}", name);
    }

    @Override
    public void addSatelliteToConstellation(Long constellationId, Long satelliteId) {
        SatelliteConstellation constellation = constellationRepository.findById(constellationId)
                .orElseThrow(() -> new SpaceOperationException("Не существует группировки c id = " + constellationId));
        Satellite satellite = satelliteRepository.findById(satelliteId)
                .orElseThrow(() -> new SpaceOperationException("Не существует спутника c id = " + satelliteId));

        constellation.addSatellite(satellite);
        satelliteRepository.save(satellite);
        log.info("{} добавлен в группировку \"{}\"", satellite.getName(), constellation.getName());
    }

    @Override
    public void executeConstellationMission(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellationByName(constellationName);
        log.info("ВЫПОЛНЕНИЕ МИССИЙ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        constellation.executeAllMissions();
    }

    @Override
    public void activateAllSatellites(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation constellation = getConstellationByName(constellationName);
        log.info("АКТИВАЦИЯ СПУТНИКОВ ГРУППИРОВКИ {}", constellationName.toUpperCase());
        log.info("=".repeat(50));
        constellation.activateAllSatellites();
    }

    @Override
    public void showConstellationStatus(String constellationName) {
        validateName(constellationName);

        SatelliteConstellation satelliteConstellation = getConstellationByName(constellationName);
        log.info("{}", satelliteConstellation.getAllSatellitesStatuses());
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new SpaceOperationException("Имя группировки не может быть пустым");
        }
    }
}
