package seminars.domains.satellites;

import lombok.Getter;
import lombok.ToString;

import static seminars.constants.CommunicationSatelliteConstants.SEND_DATA_ENERGY_CONSUMPTION;

@Getter
@ToString
public class CommunicationSatellite extends Satellite {
    private final double bandwidth;

    public CommunicationSatellite(String name, double batteryLevel, double bandwidth) {
        super(name, batteryLevel);
        this.bandwidth = bandwidth;
    }

    @Override
    public void performMission() {
        if (state.isActive()) {
            System.out.println(name + ": Передача данных со скоростью " + bandwidth + " Мбит/с");
            sendData(bandwidth);
            energy.consume(SEND_DATA_ENERGY_CONSUMPTION);
        } else {
            System.out.println(name + " не может выполнить передачу данных - не активен");
        }
    }

    private void sendData(double amountOfData) {
        if (state.isActive()) {
            System.out.println(name + ": Отправил " + amountOfData + " Мбит данных!");
        }
    }

}
