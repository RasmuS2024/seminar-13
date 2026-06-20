package seminars.services;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "active_satellite")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveSatellite {

    @Id
    @Column(name = "satellite_id")
    private Long satelliteId;
}
