package io.github.betterclient.crosshairutils.config;

public enum CrosshairMode {
    NORMAL("Normal"), ATTACK_ENTITY("Attacking Entity"), ATTACK_BLOCK("Attacking Block");

    public final String text;
    CrosshairMode(String text) {
        this.text = text;
    }
}
