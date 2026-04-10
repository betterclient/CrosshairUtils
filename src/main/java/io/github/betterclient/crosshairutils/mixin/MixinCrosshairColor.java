package io.github.betterclient.crosshairutils.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.betterclient.crosshairutils.Config;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class MixinCrosshairColor {
    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 0))
    public void onRenderCrosshair(GuiGraphics instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        instance.blitSprite(renderPipeline, identifier, i, j, k, l, ARGB.color(
                Config.crosshairColor.alpha(), Config.crosshairColor.red(), Config.crosshairColor.green(), Config.crosshairColor.blue()
        ));
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 1))
    public void onRenderAttackIndicator(GuiGraphics instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        instance.blitSprite(renderPipeline, identifier, i, j, k, l, ARGB.color(
                Config.attackIndicatorColor.alpha(), Config.attackIndicatorColor.red(), Config.attackIndicatorColor.green(), Config.attackIndicatorColor.blue()
        ));
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 2))
    public void onRenderAttackIndicator0(GuiGraphics instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        instance.blitSprite(renderPipeline, identifier, i, j, k, l, ARGB.color(
                Config.attackIndicatorColor.alpha(), Config.attackIndicatorColor.red(), Config.attackIndicatorColor.green(), Config.attackIndicatorColor.blue()
        ));
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIIIIII)V"))
    public void onRenderAttackIndicator(GuiGraphics instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l, int m, int n, int o, int p) {
        instance.blitSprite(renderPipeline, identifier, i, j, k, l, m, n, o, p, ARGB.color(
                Config.attackIndicatorColor.alpha(), Config.attackIndicatorColor.red(), Config.attackIndicatorColor.green(), Config.attackIndicatorColor.blue()
        ));
    }
}
