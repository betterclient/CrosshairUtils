package io.github.betterclient.crosshairutils.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.betterclient.crosshairutils.CrosshairUtils;
import io.github.betterclient.crosshairutils.config.Config;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(Gui.class)
public class MixinCrosshairColor {
    @Unique
    private static final Config config = Config.getInstance();

    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0))
    public void onRenderCrosshair(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height) {
        Color crosshairColor = config.getCrosshairColor();
        if (CrosshairUtils.customTextures.contains(location)) {
            instance.blit(
                    renderPipeline,
                    location,
                    x, y, 0f, 0f,
                    width, height, width, height, width, height
                    -1 //custom crosshairs are already colored
            );
        } else {
            instance.blitSprite(renderPipeline, location, x, y, width, height, crosshairColor.getRGB());
        }
    }

    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1))
    public void onRenderAttackIndicator(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height) {
        Color indicatorColor = config.getAttackIndicatorColor();
        instance.blitSprite(renderPipeline, location, x, y, width, height, indicatorColor.getRGB());
    }

    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 2))
    public void onRenderAttackIndicator0(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier location, int x, int y, int width, int height) {
        Color indicatorColor = config.getAttackIndicatorColor();
        instance.blitSprite(renderPipeline, location, x, y, width, height, indicatorColor.getRGB());
    }

    @Redirect(method = "extractCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"))
    public void onRenderAttackIndicator(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier location, int spriteWidth, int spriteHeight, int textureX, int textureY, int x, int y, int width, int height) {
        Color indicatorColor = config.getAttackIndicatorColor();
        instance.blitSprite(renderPipeline, location, spriteWidth, spriteHeight, textureX, textureY, x, y, width, height, indicatorColor.getRGB());
    }
}
