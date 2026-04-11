package io.github.betterclient.crosshairutils.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
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

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 0))
    public void onRenderCrosshair(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        Color crosshairColor = config.getCrosshairColor();
        if (CrosshairUtils.customTextures.contains(resourceLocation)) {
            instance.blit(
                    renderPipeline,
                    resourceLocation,
                    i, j, 0f, 0f,
                    k, l, k, l, k, l,
                    -1 //custom crosshairs are already colored
            );
        } else {
            instance.blitSprite(renderPipeline, resourceLocation, i, j, k, l, crosshairColor.getRGB());
        }
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    public void onRenderAttackIndicator(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        Color indicatorColor = config.getAttackIndicatorColor();
        instance.blitSprite(renderPipeline, resourceLocation, i, j, k, l, indicatorColor.getRGB());
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    public void onRenderAttackIndicator0(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l) {
        Color indicatorColor = config.getAttackIndicatorColor();
        instance.blitSprite(renderPipeline, resourceLocation, i, j, k, l, indicatorColor.getRGB());
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    public void onRenderAttackIndicator(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation resourceLocation, int i, int j, int k, int l, int m, int n, int o, int p) {
        int color = config.getAttackIndicatorColor().getRGB();
        instance.blitSprite(renderPipeline, resourceLocation, i, j, k, l, m, n, o, p, color);
    }
}
