package io.github.betterclient.crosshairutils.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ConfigScreen extends Screen {
    Screen parent;
    public ConfigScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x = 10;
        int y = 10;
        int spacing = 24;
        int width = 150;
        int height = 20;

        addRenderableWidget(Checkbox
                .builder(Component.literal("Render crosshair"), this.font)
                .pos(x, y)
                .onValueChange((checkbox, bl) -> Config.getInstance().setShouldRenderCrosshair(bl))
                .selected(Config.getInstance().shouldRenderCrosshair())
                .build()
        );

        y += spacing;

        addRenderableWidget(Button
                .builder(Component.literal("Blend mode: " + Config.getInstance().blendModeStr()), button -> {
                    int ordinal = Config.getInstance().getCrosshairBlendMode().ordinal();
                    Config.BlendMode value = Config.BlendMode.values()[ordinal == Config.BlendMode.values().length - 1 ? 0 : ordinal + 1];

                    Config.getInstance().setCrosshairBlendMode(value);
                    button.setMessage(Component.literal("Blend mode: " + Config.getInstance().blendModeStr()));
                })
                .pos(x, y)
                .size(width, height)
                .build()
        );

        y += spacing;

        addRenderableWidget(new StringWidget(x, y, width, height, Component.literal("Crosshair color (Hex):"), this.font));
        y += 16;

        EditBox crosshairHexBox = new EditBox(this.font, x, y, width, height, Component.literal("Crosshair color"));
        crosshairHexBox.setMaxLength(7);
        crosshairHexBox.setFilter(s -> s.matches("^#?[0-9a-fA-F]*$"));
        crosshairHexBox.setValue(String.format("#%06x", Config.getInstance().getCrosshairColorJava().getRGB() & 0xFFFFFF));
        crosshairHexBox.setResponder(value -> {
            try {
                Config.getInstance().setCroshairColor(Color.decode(value));
            } catch (Exception ignored) {} //ignore normal typing
        });
        addRenderableWidget(crosshairHexBox);

        y += spacing + 4;

        addRenderableWidget(new StringWidget(x, y, width, height, Component.literal("Attack indicator color (Hex):"), this.font));
        y += 16;

        EditBox attackIndicatorHexBox = new EditBox(this.font, x, y, width, height, Component.literal("Attack indicator color"));
        attackIndicatorHexBox.setMaxLength(7);
        attackIndicatorHexBox.setFilter(s -> s.matches("^#?[0-9a-fA-F]*$"));
        attackIndicatorHexBox.setValue(String.format("#%06x", Config.getInstance().getAttackIndicatorColorJava().getRGB() & 0xFFFFFF));
        attackIndicatorHexBox.setResponder(value -> {
            try {
                Config.getInstance().setAttackIndicatorColor(Color.decode(value));
            } catch (Exception ignored) {} //ignore normal typing
        });
        addRenderableWidget(attackIndicatorHexBox);

        int bottomY = this.height - 30;

        addRenderableWidget(Button
                .builder(Component.literal("Edit crosshair shape"), button -> {
                    Minecraft.getInstance().setScreen(new EditShapeScreen(this));
                })
                .pos(this.width / 2 - 155, bottomY)
                .size(150, 20)
                .build()
        );

        addRenderableWidget(Button
                .builder(Component.literal("Done"), button -> {
                    Minecraft.getInstance().setScreen(parent);
                })
                .pos(this.width / 2 + 5, bottomY)
                .size(150, 20)
                .build()
        );
    }
}
