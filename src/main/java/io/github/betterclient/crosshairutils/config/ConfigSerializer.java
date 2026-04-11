package io.github.betterclient.crosshairutils.config;

import io.github.betterclient.crosshairutils.CrosshairUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.json.JSONObject;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ConfigSerializer {
    public static void readConfig(Config cfg) {
        Path config = FabricLoader.getInstance().getConfigDir().resolve("crosshairutils.json");
        if (!config.toFile().exists()) return;
        JSONObject obj;
        try {
            obj = new JSONObject(Files.readString(config));
        } catch (Exception e) {
            return;
        }

        cfg.setShouldRenderCrosshair(obj.optBoolean("renderCrosshair", cfg.shouldRenderCrosshair()));
        cfg.setCrosshairBlendMode(BlendMode.valueOf(obj.optString("crosshairBlendMode", "INVERT")));

        cfg.setCrosshairColor(new Color(obj.optInt("crosshairColor", -1), true));
        cfg.setAttackIndicatorColor(new java.awt.Color(obj.optInt("attackIndicatorColor", -1), true));

        cfg.setShape(readFromStr(obj.optString("shape", "vanilla")));
        int[][] customShape = cfg.getCustomShape(CrosshairMode.NORMAL);
        readFromStr(obj.optString("customShape", "0"), customShape);

        cfg.setShapeAttackBlock(readFromStr(obj.optString("shapeAttackBlock", "vanilla")));
        customShape = cfg.getCustomShape(CrosshairMode.ATTACK_BLOCK);
        readFromStr(obj.optString("customBlockShape", "0"), customShape);

        cfg.setShapeAttackEntity(readFromStr(obj.optString("shapeAttackEntity", "vanilla")));
        customShape = cfg.getCustomShape(CrosshairMode.ATTACK_ENTITY);
        readFromStr(obj.optString("customEntityShape", "0"), customShape);
    }

    public static void saveConfig(Config config) {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("crosshairutils.json");
        try {
            JSONObject obj = new JSONObject();
            obj.put("renderCrosshair", config.shouldRenderCrosshair());
            obj.put("crosshairBlendMode", config.getCrosshairBlendMode().toString());
            obj.put("crosshairColor", config.getCrosshairColor().getRGB());
            obj.put("attackIndicatorColor", config.getAttackIndicatorColor().getRGB());

            obj.put("shape", toStr(config.getShape()));
            obj.put("customShape", toStr0(config.getCustomShape(CrosshairMode.NORMAL)));

            obj.put("shapeAttackEntity", toStr(config.getShapeAttackEntity()));
            obj.put("customEntityShape", toStr0(config.getCustomShape(CrosshairMode.ATTACK_ENTITY)));

            obj.put("shapeAttackBlock", toStr(config.getShapeAttackBlock()));
            obj.put("customBlockShape", toStr0(config.getCustomShape(CrosshairMode.ATTACK_BLOCK)));

            if (Files.exists(configPath)) Files.delete(configPath);
            Files.writeString(configPath, obj.toString(4));
        } catch (Exception e) {
            CrosshairUtils.LOGGER.error("Failed to save config", e);
        }
    }

    public static String toStr0(int[][] shape) {
        int rows = shape.length, cols = rows > 0 ? shape[0].length : 0;
        ByteBuffer buffer = ByteBuffer.allocate(8 + (rows * cols * 4));
        buffer.putInt(rows);
        buffer.putInt(cols);
        for (int[] row : shape) {
            for (int cell : row) {
                buffer.putInt(cell);
            }
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(buffer.array());
            gzip.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            return "0";
        }
    }

    public static void readFromStr(String str, int[][] defaultValue) {
        if (str.equals("0")) return;
        try {
            byte[] compressed = Base64.getDecoder().decode(str);

            ByteArrayInputStream bais = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(bais);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] tempBuffer = new byte[1024];
            int len;
            while ((len = gzip.read(tempBuffer)) > 0) {
                out.write(tempBuffer, 0, len);
            }
            byte[] rawData = out.toByteArray();

            ByteBuffer buffer = ByteBuffer.wrap(rawData);
            int rows = buffer.getInt();
            int cols = buffer.getInt();

            for (int i = 0; i < rows && i < defaultValue.length; i++) {
                for (int j = 0; j < cols && j < defaultValue[i].length; j++) {
                    defaultValue[i][j] = buffer.getInt();
                }
            }
        } catch (Exception ignored) {}
    }

    public static CrossShape readFromStr(String s) {
        return switch (s.toLowerCase()) {
            case "arrow" -> CrossShape.ARROW;
            case "dot" -> CrossShape.DOT;
            case "circle" -> CrossShape.CIRCLE;
            case "custom" -> CrossShape.CUSTOM;
            default -> CrossShape.VANILLA;
        };
    }

    public static String toStr(CrossShape shape) {
        return switch (shape) {
            case ARROW -> "arrow";
            case DOT -> "dot";
            case CIRCLE -> "circle";
            case CUSTOM -> "custom";
            default -> "vanilla";
        };
    }
}
