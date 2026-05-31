package seminars.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import seminars.domains.constellations.SatelliteConstellation;

import java.util.List;
import java.util.Optional;

public interface ConstellationRepository extends JpaRepository<SatelliteConstellation, Long> {
    @EntityGraph(attributePaths = {"satellites", "satellites.energy"})
    Optional<SatelliteConstellation> findByName(String name);

    @Override
    @EntityGraph(attributePaths = {"satellites", "satellites.energy"})
    List<SatelliteConstellation> findAll();

    boolean existsByName(String name);

    @Modifying
    @Transactional
    void deleteByName(String name);
}
