package io.github.betterclient.crosshairutils.mixin;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.betterclient.crosshairutils.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Gui.class)
public class MixinGui {
    @Unique
    private static final RenderPipeline CROSSHAIR_ADDITIVE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                    .withLocation("pipeline/crosshair_additive")
                    .withBlend(BlendFunction.ADDITIVE)
                    .build()
    );

    @Unique
    private static final RenderPipeline CROSSHAIR_NORMAL = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
                    .withLocation("pipeline/crosshair_normal")
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .build()
    );

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void onRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!Config.renderCrosshair) {
            ci.cancel();
        }
    }



    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderPipelines;CROSSHAIR:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.GETSTATIC))
    public RenderPipeline injectCustomPipeline() {
        return switch (Config.crosshairBlendMode) {
            case NORMAL -> CROSSHAIR_NORMAL;
            case ADDITIVE -> CROSSHAIR_ADDITIVE;
            case INVERT -> RenderPipelines.CROSSHAIR;
        };
    }
}
