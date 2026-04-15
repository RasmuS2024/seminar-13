package seminars.domains.satellites;

import lombok.Getter;
import lombok.ToString;

import static seminars.constants.ImagingSatelliteConstants.PHOTO_ENERGY_CONSUMPTION;

@Getter
@ToString
public class ImagingSatellite extends Satellite{
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
            System.out.println(name + ": Съемка территории с разрешением " + resolution + " м/пиксель");
            takePhoto();
            energy.consume(PHOTO_ENERGY_CONSUMPTION);
        } else {
            System.out.println(name + ": не может выполнить съемку - не активен");
        }
    }

    private void takePhoto() {
        if (state.isActive()) {
            photosTaken++;
            System.out.println(name + ": Снимок #" + photosTaken + " сделан!");
        }
    }

}
