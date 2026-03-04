package seminars.domains.satellites;

import lombok.Getter;

@Getter
public class ImagingSatellite extends Satellite{
    private final double resolution;
    private int photosTaken;
    private static final double PHOTO_ENERGY_CONSUMPTION = 0.07;    //количество энергии для фотографирования

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

    @Override
    public String toString() {
        return "ImagingSatellite{" +
                "resolution=" + resolution +
                ", photosTaken=" + photosTaken +
                ", name='" + name +
                "', state=" + state +
                ", energy=" + energy +
                "}";
    }
}
