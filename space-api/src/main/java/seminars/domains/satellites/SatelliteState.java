package seminars.domains.satellites;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Embeddable
public class SatelliteState {
    @JsonProperty("isActive")
    protected boolean isActive = false;
    private String statusMessage;

    public SatelliteState() {
        this.statusMessage = "Не активирован";
    }

    public boolean activate(boolean hasSufficientPower) {
        if (isActive) {
            return true;
        }
        if (hasSufficientPower) {
            isActive = true;
            statusMessage = "Активен";
            return true;
        }
        statusMessage = "Недостаточно энергии";
        return false;
    }

    public void deActivate() {
        isActive = false;
        statusMessage = "Деактивирован";
    }

}
