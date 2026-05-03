package seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seminars.domains.constellations.SatelliteConstellation;
import java.util.Optional;

public interface ConstellationRepository extends JpaRepository<SatelliteConstellation, Long> {
    Optional<SatelliteConstellation> findByConstellationName(String name);
    boolean existsByConstellationName(String name);
    void deleteByConstellationName(String name);
}
