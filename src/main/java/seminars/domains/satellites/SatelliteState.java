package seminars.domains.satellites;

import lombok.Data;

@Data
public class SatelliteState {
    protected boolean isActive = false;
    private String statusMessage;

    public SatelliteState() {
        this.statusMessage = "Не активирован";
    }

    public boolean activate(boolean hasSufficientPower) {

        if (hasSufficientPower && !isActive) {
            isActive = true;
            statusMessage = "Активен";
            return true;
        }
        statusMessage = hasSufficientPower ? "Уже активен" : "Недостаточно энергии";
        return false;
    }

    public void deActivate() {
        isActive = false;
        statusMessage = "Деактивирован";
    }

}
