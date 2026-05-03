package seminars.domains.satellites;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

import static seminars.constants.CommunicationSatelliteConstants.SEND_DATA_ENERGY_CONSUMPTION;

@Slf4j
@Getter
@ToString
@Entity
@DiscriminatorValue("COMMUNICATION")
@NoArgsConstructor
public class CommunicationSatellite extends Satellite {
    private double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @Override
    public void performMission() {
        if (state.isActive()) {
            log.info("{}: Передача данных со скоростью {} Мбит/с", name, bandwidth);
            sendData(bandwidth);
            energy.consume(SEND_DATA_ENERGY_CONSUMPTION);
        } else {
            log.info("{} не может выполнить передачу данных - не активен", name);
        }
    }

    private void sendData(double amountOfData) {
        if (state.isActive()) {
            log.info("{}: Отправил {} Мбит данных!", name, amountOfData);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommunicationSatellite that)) {
            return false;
        }
        return getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

}
