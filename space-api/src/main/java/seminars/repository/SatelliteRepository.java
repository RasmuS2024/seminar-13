package seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seminars.domains.satellites.Satellite;
import java.util.List;
import java.util.Optional;

public interface SatelliteRepository extends JpaRepository<Satellite, Long> {
    List<Satellite> findByConstellationId(Long constellationId);
    Optional<Satellite> findByNameAndConstellationId(String name, Long constellationId);
}
