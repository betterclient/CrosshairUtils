package io.github.betterclient.crosshairutils;

public class Config {
    public static boolean renderCrosshair = true;
    public static BlendMode crosshairBlendMode = BlendMode.ADDITIVE;

    public static Color crosshairColor = new Color(255, 0, 255, 255);
    public static Color attackIndicatorColor = new Color(255, 0, 0, 255);

    public enum BlendMode {
        NORMAL, ADDITIVE, INVERT,
    }
    public record Color(int red, int green, int blue, int alpha) { }
}
