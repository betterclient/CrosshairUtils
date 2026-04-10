package io.github.betterclient.crosshairutils.config;

import io.github.betterclient.crosshairutils.CrosshairUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static io.github.betterclient.crosshairutils.CrosshairUtils.CUSTOM_TEXTURE_SIZE;

public class Config {
    private boolean renderCrosshair = true;
    private BlendMode crosshairBlendMode = BlendMode.INVERT;

    private Color crosshairColor = new Color(255, 255, 255, 255);
    private Color attackIndicatorColor = new Color(255, 255, 255, 255);

    private CrossShape shape = CrossShape.VANILLA;
    private boolean[][] customShape = new boolean[CUSTOM_TEXTURE_SIZE][CUSTOM_TEXTURE_SIZE];

    private CrossShape shapeAttackEntity = CrossShape.VANILLA;
    private boolean[][] customEntityShape = new boolean[CUSTOM_TEXTURE_SIZE][CUSTOM_TEXTURE_SIZE];

    private CrossShape shapeAttackBlock = CrossShape.VANILLA;
    private boolean[][] customBlockShape = new boolean[CUSTOM_TEXTURE_SIZE][CUSTOM_TEXTURE_SIZE];

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

    public boolean[][] getCustomShape(EditShapeScreen.Mode mode) {
        return switch (mode) {
            case NORMAL -> customShape;
            case ATTACK_ENTITY -> customEntityShape;
            case ATTACK_BLOCK -> customBlockShape;
        };
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
        customShape = fromStr(obj.optString("customShape", "0"), customShape);

        shapeAttackEntity = fromStr(obj.optString("shapeAttackEntity", "vanilla"));
        customEntityShape = fromStr(obj.optString("customEntityShape", "0"), customEntityShape);

        shapeAttackBlock = fromStr(obj.optString("shapeAttackBlock", "vanilla"));
        customBlockShape = fromStr(obj.optString("customBlockShape", "0"), customBlockShape);
    }

    private CrossShape fromStr(String s) {
        return switch (s.toLowerCase()) {
            case "arrow" -> CrossShape.ARROW;
            case "dot" -> CrossShape.DOT;
            case "circle" -> CrossShape.CIRCLE;
            case "custom" -> CrossShape.CUSTOM;
            default -> CrossShape.VANILLA;
        };
    }

    private String toStr(CrossShape shape) {
        return switch (shape) {
            case ARROW -> "arrow";
            case DOT -> "dot";
            case CIRCLE -> "circle";
            case CUSTOM -> "custom";
            default -> "vanilla";
        };
    }

    String toStr0(boolean[][] shape) {
        int rows = shape.length, cols = rows > 0 ? shape[0].length : 0;
        byte[] data = new byte[2 + (rows * cols + 7) / 8];
        data[0] = (byte) rows;
        data[1] = (byte) cols;
        int bit = 0;
        for (boolean[] row : shape)
            for (boolean cell : row) {
                if (cell) data[2 + bit / 8] |= 1 << (bit % 8);
                bit++;
            }
        return Base64.getEncoder().encodeToString(data);
    }

    boolean[][] fromStr(String str, boolean[][] defaultValue) {
        if (str.equals("0")) return defaultValue;
        try {
            byte[] data = Base64.getDecoder().decode(str);
            int rows = data[0] & 0xFF, cols = data[1] & 0xFF;
            boolean[][] shape = new boolean[rows][cols];
            int bit = 0;
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < cols; j++)
                    shape[i][j] = (data[2 + bit / 8] & (1 << (bit++ % 8))) != 0;
            return shape;
        } catch (Exception e) {
            return defaultValue;
        }
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
            obj.put("customShape", toStr0(customShape));

            obj.put("shapeAttackEntity", toStr(shapeAttackEntity));
            obj.put("customEntityShape", toStr0(customEntityShape));

            obj.put("shapeAttackBlock", toStr(shapeAttackBlock));
            obj.put("customBlockShape", toStr0(customBlockShape));

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
        VANILLA, ARROW, DOT, CIRCLE, CUSTOM
    }
}
