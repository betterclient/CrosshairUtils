package io.github.betterclient.crosshairutils.config;

import io.github.betterclient.crosshairutils.CrosshairUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.json.JSONObject;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {
    private boolean renderCrosshair = true;
    private BlendMode crosshairBlendMode = BlendMode.INVERT;

    private Color crosshairColor = new Color(255, 255, 255, 255);
    private Color attackIndicatorColor = new Color(255, 255, 255, 255);

    private CrossShape shape = CrossShape.VANILLA;
    private CrossShape shapeAttackEntity = CrossShape.VANILLA;
    private CrossShape shapeAttackBlock = CrossShape.VANILLA;

    public boolean shouldRenderCrosshair() {
        return renderCrosshair;
    }
    public BlendMode getCrosshairBlendMode() {
        return crosshairBlendMode;
    }
    public Color getCrosshairColor() {
        return crosshairColor;
    }
    public Color getAttackIndicatorColor() {
        return attackIndicatorColor;
    }

    public java.awt.Color getCrosshairColorJava() {
        return new java.awt.Color(crosshairColor.red(), crosshairColor.green(), crosshairColor.blue(), crosshairColor.alpha());
    }

    public java.awt.Color getAttackIndicatorColorJava() {
        return new java.awt.Color(attackIndicatorColor.red(), attackIndicatorColor.green(), attackIndicatorColor.blue(), attackIndicatorColor.alpha());
    }

    public void setShouldRenderCrosshair(boolean bl) {
        this.renderCrosshair = bl;
    }

    public String blendModeStr() {
        return switch (crosshairBlendMode) {
            case NORMAL -> "Normal";
            case INVERT -> "Invert (default)";
            case ADDITIVE -> "Additive";
        };
    }

    public void setCrosshairBlendMode(BlendMode value) {
        this.crosshairBlendMode = value;
    }

    public void setCroshairColor(java.awt.Color color) {
        this.crosshairColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setAttackIndicatorColor(java.awt.Color color) {
        this.attackIndicatorColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public CrossShape getShape() {
        return shape;
    }

    public void setShape(CrossShape shape) {
        this.shape = shape;
    }

    public CrossShape getShapeAttackEntity() {
        return shapeAttackEntity;
    }

    public void setShapeAttackEntity(CrossShape shapeAttackEntity) {
        this.shapeAttackEntity = shapeAttackEntity;
    }

    public CrossShape getShapeAttackBlock() {
        return shapeAttackBlock;
    }

    public void setShapeAttackBlock(CrossShape shapeAttackBlock) {
        this.shapeAttackBlock = shapeAttackBlock;
    }

    private static Config instance;
    public static Config getInstance() {
        if (instance == null) {
            //readConfig
            instance = new Config();
            instance.readConfig();
        }
        return instance;
    }

    public void readConfig() {
        Path config = FabricLoader.getInstance().getConfigDir().resolve("crosshairutils.json");
        if (!config.toFile().exists()) return;
        JSONObject obj;
        try {
            obj = new JSONObject(Files.readString(config));
        } catch (Exception e) {
            return;
        }

        renderCrosshair = obj.optBoolean("renderCrosshair", renderCrosshair);
        crosshairBlendMode = BlendMode.valueOf(obj.optString("crosshairBlendMode", "INVERT"));

        java.awt.Color c = new java.awt.Color(obj.optInt("crosshairColor", -1), true);
        crosshairColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());

        java.awt.Color a = new java.awt.Color(obj.optInt("attackIndicatorColor", -1), true);
        attackIndicatorColor = new Color(a.getRed(), a.getGreen(), a.getBlue(), a.getAlpha());

        shape = fromStr(obj.optString("shape", "vanilla"));
        shapeAttackEntity = fromStr(obj.optString("shapeAttackEntity", "vanilla"));
        shapeAttackBlock = fromStr(obj.optString("shapeAttackBlock", "vanilla"));
    }

    private CrossShape fromStr(String s) {
        return switch (s.toLowerCase()) {
            case "arrow" -> CrossShape.ARROW;
            case "dot" -> CrossShape.DOT;
            case "circle" -> CrossShape.CIRCLE;
            default -> CrossShape.VANILLA;
        };
    }

    private String toStr(CrossShape shape) {
        return switch (shape) {
            case ARROW -> "arrow";
            case DOT -> "dot";
            case CIRCLE -> "circle";
            default -> "vanilla";
        };
    }

    public void saveConfig() {
        Path config = FabricLoader.getInstance().getConfigDir().resolve("crosshairutils.json");
        try {
            JSONObject obj = new JSONObject();
            obj.put("renderCrosshair", renderCrosshair);
            obj.put("crosshairBlendMode", crosshairBlendMode.toString());
            obj.put("crosshairColor", new java.awt.Color(
                    crosshairColor.red, crosshairColor.green, crosshairColor.blue, crosshairColor.alpha
            ).getRGB());
            obj.put("attackIndicatorColor", new java.awt.Color(
                    attackIndicatorColor.red, attackIndicatorColor.green, crosshairColor.blue, crosshairColor.alpha
            ).getRGB());
            obj.put("shape", toStr(shape));
            obj.put("shapeAttackEntity", toStr(shapeAttackEntity));
            obj.put("shapeAttackBlock", toStr(shapeAttackBlock));

            if (Files.exists(config)) Files.delete(config);
            Files.writeString(config, obj.toString(4));
        } catch (Exception e) {
            CrosshairUtils.LOGGER.error("Failed to save config", e);
        }
    }

    public enum BlendMode {
        NORMAL, ADDITIVE, INVERT,
    }
    public record Color(int red, int green, int blue, int alpha) { }
    public enum CrossShape {
        VANILLA, ARROW, DOT, CIRCLE
    }
}
