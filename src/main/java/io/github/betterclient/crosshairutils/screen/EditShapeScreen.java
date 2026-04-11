package io.github.betterclient.crosshairutils.screen;

import io.github.betterclient.crosshairutils.CrosshairUtils;
import io.github.betterclient.crosshairutils.config.Config;
import io.github.betterclient.crosshairutils.config.ConfigSerializer;
import io.github.betterclient.crosshairutils.config.CrossShape;
import io.github.betterclient.crosshairutils.config.CrosshairMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static io.github.betterclient.crosshairutils.CrosshairUtils.CUSTOM_TEXTURE_SIZE;

public class EditShapeScreen extends Screen {
    private final Screen parent;
    private CrosshairMode currentMode = CrosshairMode.NORMAL;
    private final Config config = Config.getInstance();
    private boolean mouseLeftDown = false;
    private boolean mouseRightDown = false;
    private int selectedColor = -1;

    public EditShapeScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        if (getShape() != CrossShape.CUSTOM) return;

        int leftX = 10;
        int iconSize = 20;
        int indicatorY = 15;

        guiGraphics.drawString(this.font, "Selected color", leftX, indicatorY - 10, Color.WHITE.getRGB());
        guiGraphics.fill(leftX - 1, indicatorY - 1, leftX + iconSize + 1, indicatorY + iconSize + 1, Color.GRAY.getRGB());
        guiGraphics.fill(leftX, indicatorY, leftX + iconSize, indicatorY + iconSize, selectedColor);

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
                        customShape[x][y] = selectedColor;
                        color = selectedColor;
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
    public boolean mouseClicked(double d, double e, int i) {
        if (i == 0) {
            mouseLeftDown = true;
        } else if (i == 1) {
            mouseRightDown = true;
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (i == 0) {
            mouseLeftDown = false;
        } else if (i == 1) {
            mouseRightDown = false;
        }
        return super.mouseReleased(d, e, i);
    }

    @Override
    public void removed() {
        CrosshairUtils.load(config.getCustomShape(CrosshairMode.NORMAL), CrosshairUtils.missTexture);
        CrosshairUtils.load(config.getCustomShape(CrosshairMode.ATTACK_ENTITY), CrosshairUtils.entityTexture);
        CrosshairUtils.load(config.getCustomShape(CrosshairMode.ATTACK_BLOCK), CrosshairUtils.blockTexture);
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
                    currentMode = CrosshairMode.values()[currentMode.ordinal() == CrosshairMode.values().length - 1 ? 0 : currentMode.ordinal() + 1];
                    clearWidgets();
                    init();
                })
                .pos(this.width / 2 - 155, topY)
                .size(150, 20)
                .build()
        );

        addRenderableWidget(Button
                .builder(Component.literal("Shape: " + getShape().name()), button -> {
                    setShape(CrossShape.values()[getShape().ordinal() == CrossShape.values().length - 1 ? 0 : getShape().ordinal() + 1]);

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

        if (getShape() != CrossShape.CUSTOM) return;

        initRightButtons();
        initLeftWidgets();
    }
    private void initLeftWidgets() {
        int iconSize = 20, spacing = 4;
        int leftX = 10;
        int numLeftButtons = 4;
        int startY = (this.height - (numLeftButtons * iconSize + (numLeftButtons - 1) * spacing)) / 2;

        addIconButton(
                leftX, startY, iconSize, null,
                ResourceLocation.tryBuild("crosshairutils", "menu/white"),
                button -> {
                    selectedColor = Color.WHITE.getRGB();
                }
        );
        int index = 1;

        addIconButton(
                leftX, startY + ((index++) * (iconSize + spacing)), iconSize, null,
                ResourceLocation.tryBuild("crosshairutils", "menu/red"),
                button -> {
                    selectedColor = Color.RED.getRGB();
                }
        );

        addIconButton(
                leftX, startY + ((index++) * (iconSize + spacing)), iconSize, null,
                ResourceLocation.tryBuild("crosshairutils", "menu/green"),
                button -> {
                    selectedColor = Color.GREEN.getRGB();
                }
        );

        addIconButton(
                leftX, startY + ((index++) * (iconSize + spacing)), iconSize, null,
                ResourceLocation.tryBuild("crosshairutils", "menu/blue"),
                button -> {
                    selectedColor = Color.BLUE.getRGB();
                }
        );

        int hexY = startY + ((index + 1) * (iconSize + spacing));

        EditBox hexBox = new EditBox(this.font, leftX, hexY, 50, iconSize, Component.literal("Hex Color"));
        hexBox.setMaxLength(9);
        hexBox.setValue("#FFFFFF");
        hexBox.setFilter(s -> s.matches("^#?[0-9a-fA-F]*$"));
        addRenderableWidget(hexBox);

        addRenderableWidget(Button
                .builder(Component.literal("Set"), button -> {
                    String hex = hexBox.getValue().replace("#", "").trim();
                    try {
                        if (hex.length() == 6) {
                            selectedColor = 0xFF000000 | Integer.parseInt(hex, 16);
                        } else if (hex.length() == 8) {
                            selectedColor = (int) Long.parseLong(hex, 16);
                        }
                    } catch (NumberFormatException ignored) {

                    }
                })
                .pos(leftX + 50 + 2, hexY)
                .size(30, iconSize)
                .build()
        );
    }

    private void initRightButtons() {
        //extra helper buttons
        int iconSize = 20, spacing = 5, numButtons = 4;
        int rightX = this.width - iconSize - 10;
        int startY = (this.height - (numButtons * iconSize + (numButtons - 1) * spacing)) / 2;
        int[][] realShape = config.getCustomShape(currentMode);

        addIconButton(
                rightX, startY, iconSize, "Copy code",
                ResourceLocation.tryBuild("crosshairutils", "menu/copy"),
                btn -> {
                    Minecraft.getInstance().keyboardHandler.setClipboard(ConfigSerializer.toStr0(realShape));
                }
        );

        addIconButton(
                rightX, startY + (iconSize + spacing), iconSize, "Paste code from clipboard",
                ResourceLocation.tryBuild("crosshairutils", "menu/paste"),
                btn -> {
                    try {
                        ConfigSerializer.readFromStr(Minecraft.getInstance().keyboardHandler.getClipboard(), realShape);
                    } catch (IllegalArgumentException ignored) {}
                }
        );

        addIconButton(
                rightX, startY + 2 * (iconSize + spacing), iconSize, "Reset to vanilla",
                ResourceLocation.tryBuild("minecraft", "hud/crosshair"),
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
                ResourceLocation.tryBuild("minecraft", "container/beacon/cancel"),
                btn -> {
                    askForConsent("Are you sure you wanna clear your custom crosshair?", () -> {
                        for (int[] row : realShape) Arrays.fill(row, 0);
                    });
                }
        );
    }

    private void addIconButton(int x, int y, int size, String tooltip, ResourceLocation sprite, Button.OnPress action) {
        SpriteIconButton.Builder builder = SpriteIconButton
                .builder(Component.literal(tooltip == null ? "" : tooltip), action, true)
                .size(size, size)
                .sprite(Objects.requireNonNull(sprite), 16, 16);

        SpriteIconButton btn = builder.build();
        btn.setPosition(x, y);
        addRenderableWidget(btn);
    }

    private void askForConsent(String text, Runnable task) {
        Minecraft.getInstance().setScreen(new ConfirmScreen(t -> {
            if (t) task.run();
            Minecraft.getInstance().setScreen(this);
        }, Component.empty(), Component.literal(text)));
    }

    public CrossShape getShape() {
        return switch (currentMode) {
            case NORMAL -> config.getShape();
            case ATTACK_ENTITY -> config.getShapeAttackEntity();
            case ATTACK_BLOCK -> config.getShapeAttackBlock();
        };
    }

    public void setShape(CrossShape shape) {
        switch (currentMode) {
            case NORMAL -> config.setShape(shape);
            case ATTACK_ENTITY -> config.setShapeAttackEntity(shape);
            case ATTACK_BLOCK -> config.setShapeAttackBlock(shape);
        }
    }
}
