package io.github.betterclient.crosshairutils.config;

import java.awt.*;

import static io.github.betterclient.crosshairutils.CrosshairUtils.CUSTOM_TEXTURE_SIZE;

public class Config {
    private boolean renderCrosshair = true;
    private BlendMode crosshairBlendMode = BlendMode.INVERT;

    private Color crosshairColor = new Color(255, 255, 255, 255);
    private Color attackIndicatorColor = new Color(255, 255, 255, 255);

    private CrossShape shape = CrossShape.VANILLA;
    private final int[][] customShape = new int[CUSTOM_TEXTURE_SIZE][CUSTOM_TEXTURE_SIZE];

    private CrossShape shapeAttackEntity = CrossShape.VANILLA;
    private final int[][] customEntityShape = new int[CUSTOM_TEXTURE_SIZE][CUSTOM_TEXTURE_SIZE];

    private CrossShape shapeAttackBlock = CrossShape.VANILLA;
    private final int[][] customBlockShape = new int[CUSTOM_TEXTURE_SIZE][CUSTOM_TEXTURE_SIZE];

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

    public void setCrosshairColor(Color color) {
        this.crosshairColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public void setAttackIndicatorColor(Color color) {
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

    public int[][] getCustomShape(CrosshairMode mode) {
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
            ConfigSerializer.readConfig(instance);
        }
        return instance;
    }
}
