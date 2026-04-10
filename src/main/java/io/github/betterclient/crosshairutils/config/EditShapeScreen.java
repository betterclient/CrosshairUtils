package io.github.betterclient.crosshairutils.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditShapeScreen extends Screen {
    private final Screen parent;
    private Mode currentMode = Mode.NORMAL;
    private Config config = Config.getInstance();

    public EditShapeScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);


    }

    @Override
    protected void init() {
        super.init();
        int topY = 30;
        addRenderableWidget(Button
                .builder(Component.literal("Mode: " + currentMode.text), button -> {
                    currentMode = Mode.values()[currentMode.ordinal() == Mode.values().length - 1 ? 0 : currentMode.ordinal() + 1];
                    clearWidgets();
                    init();
                })
                .pos(this.width / 2 - 155, topY)
                .size(150, 20)
                .build()
        );

        addRenderableWidget(Button
                .builder(Component.literal("Shape: " + getShape().name()), button -> {
                    setShape(Config.CrossShape.values()[getShape().ordinal() == Config.CrossShape.values().length - 1 ? 0 : getShape().ordinal() + 1]);

                    button.setMessage(Component.literal("Shape: " + getShape().name()));
                })
                .pos(this.width / 2 + 5, topY)
                .size(150, 20)
                .build()
        );

        int bottomY = this.height - 30;
        addRenderableWidget(Button
                .builder(Component.literal("Done"), button -> Minecraft.getInstance().setScreen(parent))
                .pos(this.width / 2 - 75, bottomY)
                .size(150, 20)
                .build()
        );
    }

    private enum Mode {
        NORMAL("Normal"), ATTACK_ENTITY("Attacking Entity"), ATTACK_BLOCK("Attacking Block");;

        final String text;
        Mode(String text) {
            this.text = text;
        }
    }

    public Config.CrossShape getShape() {
        return switch (currentMode) {
            case NORMAL -> config.getShape();
            case ATTACK_ENTITY -> config.getShapeAttackEntity();
            case ATTACK_BLOCK -> config.getShapeAttackBlock();
        };
    }

    public void setShape(Config.CrossShape shape) {
        switch (currentMode) {
            case NORMAL -> config.setShape(shape);
            case ATTACK_ENTITY -> config.setShapeAttackEntity(shape);
            case ATTACK_BLOCK -> config.setShapeAttackBlock(shape);
        }
    }
}
