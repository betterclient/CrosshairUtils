package io.github.betterclient.crosshairutils.screen;

import io.github.betterclient.crosshairutils.config.BlendMode;
import io.github.betterclient.crosshairutils.config.Config;
import net.minecraft.client.Minecraft;
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

        Config config = Config.getInstance();
        addRenderableWidget(Checkbox
                .builder(Component.literal("Render crosshair"), this.font)
                .pos(x, y)
                .onValueChange((checkbox, bl) -> config.setShouldRenderCrosshair(bl))
                .selected(config.shouldRenderCrosshair())
                .build()
        );

        y += spacing;

        addRenderableWidget(Button
                .builder(Component.literal("Blend mode: " + config.blendModeStr()), button -> {
                    int ordinal = config.getCrosshairBlendMode().ordinal();
                    BlendMode value = BlendMode.values()[ordinal == BlendMode.values().length - 1 ? 0 : ordinal + 1];

                    config.setCrosshairBlendMode(value);
                    button.setMessage(Component.literal("Blend mode: " + config.blendModeStr()));
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
        crosshairHexBox.setValue(String.format("#%06x", config.getCrosshairColor().getRGB() & 0xFFFFFF));
        crosshairHexBox.setTooltip(Tooltip.create(
                Component.literal("The crosshair color is inactive on custom crosshair shape")
        ));
        crosshairHexBox.setResponder(value -> {
            try {
                config.setCrosshairColor(Color.decode(value));
            } catch (Exception ignored) {} //ignore normal typing
        });
        addRenderableWidget(crosshairHexBox);

        y += spacing + 4;

        addRenderableWidget(new StringWidget(x, y, width, height, Component.literal("Attack indicator color (Hex):"), this.font));
        y += 16;

        EditBox attackIndicatorHexBox = new EditBox(this.font, x, y, width, height, Component.literal("Attack indicator color"));
        attackIndicatorHexBox.setMaxLength(7);
        attackIndicatorHexBox.setFilter(s -> s.matches("^#?[0-9a-fA-F]*$"));
        attackIndicatorHexBox.setValue(String.format("#%06x", config.getAttackIndicatorColor().getRGB() & 0xFFFFFF));
        attackIndicatorHexBox.setResponder(value -> {
            try {
                config.setAttackIndicatorColor(Color.decode(value));
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
