package pl.crewops.enums;

public enum ShiftPanelColor {
    BLACK("#000000"),
    WHITE("#FFFFFF"),
    RED("#FF0000"),
    GREEN("#00FF00"),
    BLUE("#0000FF"),
    YELLOW("#FFFF00"),
    CYAN("#00FFFF"),
    MAGENTA("#FF00FF"),
    ORANGE("#FFA500"),
    GRAY("#808080");

    private final String hex;

    ShiftPanelColor(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public String getTranslationKey() {
        return "shiftPanelColor." + name().toLowerCase(); // np. color.red, color.green, color.blue
    }

    public static ShiftPanelColor fromHex(String hex) {
        if (hex == null) return null;

        String normalized = hex.trim().toUpperCase();

        for (ShiftPanelColor color : values()) {
            if (color.getHex().equalsIgnoreCase(normalized)) {
                return color;
            }
        }
        return null;
    }
}
