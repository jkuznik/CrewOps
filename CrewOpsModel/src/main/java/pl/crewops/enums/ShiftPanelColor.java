package pl.crewops.enums;

public enum ShiftPanelColor {
    EMERALD("#2ECC71"),
    PETER_RIVER("#3498DB"),
    AMETHYST("#9B59B6"),
    SUNFLOWER("#F1C40F"),
    ORANGE("#FFA500"),
    ALIZARIN("#E74C3C"),
    WET_ASPHALT("#34495E"),
    TURQUOISE("#1ABC9C"),
    CONCRETE("#95A5A6"),
    WHITE("#FFFFFF"),
    BLACK("#000000");

    private final String hex;

    ShiftPanelColor(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public String getTranslationKey() {
        return "shiftPanelColor." + name().toLowerCase();
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
