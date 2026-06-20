package seminars.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SatelliteIdRepository {
    private final ActiveSatelliteRepository repository;

    @Transactional
    public void add(Long satelliteId) {
        if (!repository.existsById(satelliteId)) {
            repository.save(new ActiveSatellite(satelliteId));
            log.info("Спутник ID={} добавлен в реестр телеметрии", satelliteId);
        }
    }

    @Transactional
    public void remove(Long satelliteId) {
        if (repository.existsById(satelliteId)) {
            repository.deleteById(satelliteId);
            log.info("Спутник ID={} удалён из реестра телеметрии", satelliteId);
        }
    }

    @Transactional(readOnly = true)
    public Set<Long> getAll() {
        return repository.findAll().stream()
                .map(ActiveSatellite::getSatelliteId)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public boolean isEmpty() {
        return repository.count() == 0;
    }
}
