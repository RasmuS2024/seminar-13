package seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.constellations.SatelliteConstellation;
import java.util.Optional;

public interface ConstellationRepository extends JpaRepository<SatelliteConstellation, Long> {
    Optional<SatelliteConstellation> findByName(String name);

    boolean existsByName(String name);

    @Modifying
    @Transactional
    void deleteByName(String name);
}
