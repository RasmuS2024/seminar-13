package seminars.repository;

import org.springframework.stereotype.Service;
import seminars.SatelliteConstellation;
import seminars.exceptions.SpaceOperationException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ConstellationRepository {
    private final Map<String, SatelliteConstellation> constellations;

    public ConstellationRepository() {
        this.constellations = new HashMap<>();
    }

    public void addConstellation(SatelliteConstellation constellation) {
        constellations.put(constellation.getConstellationName(), constellation);
        System.out.println("Сохранена группировка: " + constellation.getConstellationName());
    }

    public SatelliteConstellation getConstellation(String name) {
        SatelliteConstellation constellation = constellations.get(name);
        if (constellation == null) {
            throw new SpaceOperationException("Группировка не найдена: " + name);
        }
        return constellation;
    }

    public void updateConstellation(String name, SatelliteConstellation updatedConstellation) {
        constellations.computeIfPresent(name, (k, v) -> updatedConstellation);
        System.out.println("Обновлена группировка: " + name);
    }

    public void deleteConstellation(String name) {
        constellations.remove(name);
        System.out.println("Удалена группировка: " + name);
    }

    public Map<String, SatelliteConstellation> getAllConstellations() {
        return new HashMap<>(constellations);
    }

    public boolean containsConstellation(String name) {
        return constellations.containsKey(name);
    }

    @Override
    public String toString() {
        if (constellations.isEmpty()) {
            return "{}";
        }

        return constellations.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(",\n\n", "{\n", "\n}"));
    }


}