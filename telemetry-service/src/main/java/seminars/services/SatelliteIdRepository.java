package seminars.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SatelliteIdRepository {
    private final Set<Long> activeIds = ConcurrentHashMap.newKeySet();

    public void add(Long satelliteId) {
        if (activeIds.add(satelliteId)) {
            log.info("Спутник ID={} добавлен в реестр телеметрии", satelliteId);
        }
    }

    public void remove(Long satelliteId) {
        if (activeIds.remove(satelliteId)) {
            log.info("Спутник ID={} удалён из реестра телеметрии", satelliteId);
        }
    }

    public Set<Long> getAll() {
        return Collections.unmodifiableSet(activeIds);
    }

    public boolean isEmpty() {
        return activeIds.isEmpty();
    }
}
