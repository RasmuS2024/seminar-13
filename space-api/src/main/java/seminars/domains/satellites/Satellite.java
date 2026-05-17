package seminars.domains.satellites;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import seminars.constants.EnergySystemConstants;
import seminars.domains.constellations.SatelliteConstellation;

@Entity
@Table(name = "satellite")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "satellite_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Satellite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    protected String name;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constellation_id")
    protected SatelliteConstellation constellation;

    @Embedded
    protected SatelliteState state;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "energy_id", unique = true)
    protected EnergySystem energy;

    protected Satellite(String name, double batteryLevel) {
        this.name = name;
        this.state = new SatelliteState();
        this.energy = EnergySystem.builder()
                .batteryLevel(batteryLevel)
                .lowBatteryThreshold(EnergySystemConstants.LOW_BATTERY_THRESHOLD)
                .maxBattery(EnergySystemConstants.MAX_BATTERY)
                .minBattery(EnergySystemConstants.MIN_BATTERY)
                .build();
    }

    public boolean activate() {
        return state.activate(energy.hasSufficientPower());
    }

    public void deActivate() {
        if (state.isActive()) {
            state.deActivate();
        }
    }

    public abstract void performMission();

}

