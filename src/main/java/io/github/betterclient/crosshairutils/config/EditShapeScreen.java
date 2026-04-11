package io.github.betterclient.crosshairutils.config;

import io.github.betterclient.crosshairutils.CrosshairUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static io.github.betterclient.crosshairutils.CrosshairUtils.CUSTOM_TEXTURE_SIZE;

public class EditShapeScreen extends Screen {
    private final Screen parent;
    private Mode currentMode = Mode.NORMAL;
    private final Config config = Config.getInstance();
    private boolean mouseLeftDown = false;
    private boolean mouseRightDown = false;

    public EditShapeScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    public void render(@NonNull GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        if (getShape() != Config.CrossShape.CUSTOM) return;

        //CUSTOM_TEXTURE_SIZE
        //render 16x16 grid
        final int GRID_HEIGHT = 9;
        final int GRID_WIDTH = 9;
        int startX = width / 2 - ((CUSTOM_TEXTURE_SIZE / 2) * GRID_WIDTH);
        int startY = height / 2 - ((CUSTOM_TEXTURE_SIZE / 2) * GRID_HEIGHT);

        int centerIndex = (CUSTOM_TEXTURE_SIZE - 1) / 2;

        //draw background
        guiGraphics.fill(
                startX - 8, startY - 8,
                startX + (CUSTOM_TEXTURE_SIZE * GRID_HEIGHT) + 8,
                startY + (CUSTOM_TEXTURE_SIZE * GRID_WIDTH) + 8,
                Color.black.getRGB()
        );

        for (int x = 0; x < CUSTOM_TEXTURE_SIZE; x++) {
            for (int y = 0; y < CUSTOM_TEXTURE_SIZE; y++) {
                int currentX = startX + x * GRID_WIDTH;
                int currentY = startY + y * GRID_HEIGHT;
                int[][] customShape = Config.getInstance().getCustomShape(currentMode);
                int color = customShape[x][y];

                if (isMouseOver(i, j, currentX, currentY, currentX + 8, currentY + 8)) {
                    if (mouseLeftDown) {
                        customShape[x][y] = -1; //todo: be able to change this
                        color = -1;
                    } else if (mouseRightDown) {
                        customShape[x][y] = 0;
                        color = 0;
                    }
                }

                guiGraphics.fill(currentX, currentY, currentX + 8, currentY + 8, color);

                if (x == centerIndex && y == centerIndex) {
                    int dotSize = 2;
                    int dotX = currentX + (8 - dotSize) / 2;
                    int dotY = currentY + (8 - dotSize) / 2;
                    guiGraphics.fill(dotX, dotY, dotX + dotSize, dotY + dotSize,
                            Color.WHITE.getRGB());
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (mouseButtonEvent.button() == 0) {
            mouseLeftDown = true;
        } else if (mouseButtonEvent.button() == 1) {
            mouseRightDown = true;
        }
        return super.mouseClicked(mouseButtonEvent, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.button() == 0) {
            mouseLeftDown = false;
        }  else if (mouseButtonEvent.button() == 1) {
            mouseRightDown = false;
        }

        return super.mouseReleased(mouseButtonEvent);
    }

    @Override
    public void removed() {
        CrosshairUtils.load(config.getCustomShape(Mode.NORMAL), CrosshairUtils.missTexture);
        CrosshairUtils.load(config.getCustomShape(Mode.ATTACK_ENTITY), CrosshairUtils.entityTexture);
        CrosshairUtils.load(config.getCustomShape(Mode.ATTACK_BLOCK), CrosshairUtils.blockTexture);
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int endX, int endY) {
        return mouseX >= x && mouseX <= endX && mouseY >= y && mouseY <= endY;
    }

    @Override
    protected void init() {
        super.init();
        int topY = 20;
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

                    clearWidgets();
                    init();
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

        if (getShape() != Config.CrossShape.CUSTOM) return;

        //extra helper buttons
        int iconSize = 20, spacing = 5, numButtons = 4;
        int rightX = this.width - iconSize - 10;
        int startY = (this.height - (numButtons * iconSize + (numButtons - 1) * spacing)) / 2;
        int[][] realShape = config.getCustomShape(currentMode);

        addIconButton(
                rightX, startY, iconSize, "Copy code",
                Identifier.tryBuild("crosshairutils", "menu/copy"),
                btn -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(config.toStr0(realShape));
                }
        );

        addIconButton(
                rightX, startY + (iconSize + spacing), iconSize, "Paste code from clipboard",
                Identifier.tryBuild("crosshairutils", "menu/paste"),
                btn -> {
                    try {
                        int[][] shape = config.fromStr(
                                Minecraft.getInstance().keyboardHandler.getClipboard(), realShape);
                        for (int x = 0; x < Math.min(shape.length, realShape.length); x++)
                            System.arraycopy(shape[x], 0, realShape[x], 0,
                                    Math.min(shape[x].length, realShape[x].length));
                    } catch (IllegalArgumentException ignored) {}
                }
        );

        addIconButton(
                rightX, startY + 2 * (iconSize + spacing), iconSize, "Reset to vanilla",
                Identifier.tryBuild("minecraft", "hud/crosshair"),
                btn -> {
                    askForConsent("Are you sure you wanna reset your custom crosshair?", () -> {
                        for (int[] row : realShape) Arrays.fill(row, 0);
                        int mid = (realShape.length - 1) / 2;
                        realShape[mid][mid] = -1;
                        for (int i = 1; i <= 4; i++) {
                            realShape[mid][mid - i] = realShape[mid][mid + i] = -1;
                            realShape[mid - i][mid] = realShape[mid + i][mid] = -1;
                        }
                    });
                }
        );

        addIconButton(
                rightX, startY + 3 * (iconSize + spacing), iconSize, "Clear",
                Identifier.tryBuild("minecraft", "container/beacon/cancel"),
                btn -> {
                    askForConsent("Are you sure you wanna clear your custom crosshair?", () -> {
                        for (int[] row : realShape) Arrays.fill(row, 0);
                    });
                }
        );
    }

    private void addIconButton(int x, int y, int size, String tooltip, Identifier sprite, Button.OnPress action) {
        SpriteIconButton btn = SpriteIconButton
                .builder(Component.literal(tooltip), action, true)
                .size(size, size)
                .withTootip()
                .sprite(Objects.requireNonNull(sprite), 16, 16)
                .build();
        btn.setPosition(x, y);
        addRenderableWidget(btn);
    }

    private void askForConsent(String text, Runnable task) {
        Minecraft.getInstance().setScreen(new ConfirmScreen(t -> {
            if (t) task.run();
            Minecraft.getInstance().setScreen(this);
        }, Component.empty(), Component.literal(text)));
    }

    public enum Mode {
        NORMAL("Normal"), ATTACK_ENTITY("Attacking Entity"), ATTACK_BLOCK("Attacking Block");

        public final String text;
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
