package seminars.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import seminars.domains.satellites.EnergySystem;

public interface EnergySystemRepository extends JpaRepository<EnergySystem, Long> {
}
