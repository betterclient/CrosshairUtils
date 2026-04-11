package io.github.betterclient.crosshairutils.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.crosshairutils.CrosshairUtils;
import io.github.betterclient.crosshairutils.config.Config;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(Gui.class)
public class MixinCrosshairColor {
    @Unique
    private static final Config config = Config.getInstance();

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    public void onRenderCrosshair(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        Color crosshairColor = config.getCrosshairColor();
        if (CrosshairUtils.customTextures.contains(resourceLocation)) {
            instance.blit(
                    resourceLocation,
                    i, j, k, l,
                    k, l, k, l
            );
        } else {
            RenderSystem.setShaderColor(crosshairColor.getRed() / 255f, crosshairColor.getGreen() / 255f, crosshairColor.getBlue() / 255f, crosshairColor.getAlpha() / 255f);
            instance.blitSprite(resourceLocation, i, j, k, l);
        }
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    public void onRenderAttackIndicator(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        Color indicatorColor = config.getAttackIndicatorColor();
        RenderSystem.setShaderColor(indicatorColor.getRed() / 255f, indicatorColor.getGreen() / 255f, indicatorColor.getBlue() / 255f, indicatorColor.getAlpha() / 255f);
        instance.blitSprite(resourceLocation, i, j, k, l);
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    public void onRenderAttackIndicator0(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        Color indicatorColor = config.getAttackIndicatorColor();
        RenderSystem.setShaderColor(indicatorColor.getRed() / 255f, indicatorColor.getGreen() / 255f, indicatorColor.getBlue() / 255f, indicatorColor.getAlpha() / 255f);
        instance.blitSprite(resourceLocation, i, j, k, l);
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    public void onRenderAttackIndicator(GuiGraphics instance, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p) {
        Color indicatorColor = config.getAttackIndicatorColor();
        RenderSystem.setShaderColor(indicatorColor.getRed() / 255f, indicatorColor.getGreen() / 255f, indicatorColor.getBlue() / 255f, indicatorColor.getAlpha() / 255f);
        instance.blitSprite(resourceLocation, i, j, k, l, m, n, o, p);
    }
}
