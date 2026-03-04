package seminars.domains.satellites;

import lombok.Getter;

public class CommunicationSatellite extends Satellite {
    @Getter
    private final double bandwidth;
    private static final double SEND_DATA_ENERGY_CONSUMPTION = 0.03;    //количество энергии для отправки данных

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

    @Override
    public String toString() {
        return "CommunicationSatellite{" +
                "bandwidth=" + bandwidth +
                ", name='" + name +
                "', state=" + state +
                ", energy=" + energy +
                "}";
    }
}
