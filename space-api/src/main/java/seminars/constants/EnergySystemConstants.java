package seminars.constants;

public final class EnergySystemConstants {
    private EnergySystemConstants() {
        /* This utility class should not be instantiated */
    }

    public static final double LOW_BATTERY_THRESHOLD = 0.15;

    public static final double MAX_BATTERY = 1.0;
    public static final double MIN_BATTERY = 0.0;

    /**
     * Для валидации.
     */
    public static final String MAX_BATTERY_STR = "1.0";

    /**
     * Для валидации.
     */
    public static final String MIN_BATTERY_STR = "0.0";

}
