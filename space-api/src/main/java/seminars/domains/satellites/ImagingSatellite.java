package seminars.domains.satellites;

import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static seminars.constants.ImagingSatelliteConstants.PHOTO_ENERGY_CONSUMPTION;

@Getter
@ToString
public class ImagingSatellite extends Satellite{
    private static final Logger log = LoggerFactory.getLogger(ImagingSatellite.class);
    private final double resolution;
    private int photosTaken;

    public ImagingSatellite(String name, double batteryLevel, double resolution) {
        super(name, batteryLevel);
        this.resolution = resolution;
        this.photosTaken = 0;
    }

    @Override
    public void performMission() {
        if (state.isActive()) {
            log.info("{}: Съемка территории с разрешением {} м/пиксель", name, resolution);
            takePhoto();
            energy.consume(PHOTO_ENERGY_CONSUMPTION);
        } else {
            log.info("{}: не может выполнить съемку - не активен", name);
        }
    }

    private void takePhoto() {
        if (state.isActive()) {
            photosTaken++;
            log.info("{}: Снимок #{} сделан!", name, photosTaken);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImagingSatellite that)) return false;
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

}
