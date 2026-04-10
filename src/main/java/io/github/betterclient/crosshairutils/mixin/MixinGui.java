package io.github.betterclient.crosshairutils.mixin;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import io.github.betterclient.crosshairutils.CrosshairUtils;
import io.github.betterclient.crosshairutils.config.Config;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Gui.class)
public class MixinGui {
    @Shadow
    @Final
    private static Identifier CROSSHAIR_SPRITE;
    @Unique
    private static final Config config = Config.getInstance();
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
                    .withBlend(new BlendFunction(SourceFactor.ONE, DestFactor.ZERO))
                    .build()
    );

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void onRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!config.shouldRenderCrosshair()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;CROSSHAIR_SPRITE:Lnet/minecraft/resources/Identifier;", opcode = Opcodes.GETSTATIC))
    public Identifier redirectCrosshairSprite() {
        Minecraft mc = Minecraft.getInstance();
        return switch (mc.hitResult.getType()) {
            case MISS -> getId(config.getShape(), CrosshairUtils.CUSTOM_MISS);
            case BLOCK -> getId(config.getShapeAttackBlock(), CrosshairUtils.CUSTOM_BLOCK);
            case ENTITY -> getId(config.getShapeAttackEntity(), CrosshairUtils.CUSTOM_ENTITY);
        };
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderPipelines;CROSSHAIR:Lcom/mojang/blaze3d/pipeline/RenderPipeline;", opcode = Opcodes.GETSTATIC))
    public RenderPipeline injectCustomPipeline() {
        return switch (config.getCrosshairBlendMode()) {
            case NORMAL -> CROSSHAIR_NORMAL;
            case ADDITIVE -> CROSSHAIR_ADDITIVE;
            case INVERT -> RenderPipelines.CROSSHAIR;
        };
    }

    @Unique
    private static final Identifier ARROW_CROSS = Identifier.tryBuild("crosshairutils", "hud/arrow_crosshair");
    @Unique
    private static final Identifier DOT_CROSS = Identifier.tryBuild("crosshairutils", "hud/dot_crosshair");
    @Unique
    private static final Identifier CIRCLE_CROSS = Identifier.tryBuild("crosshairutils", "hud/circle_crosshair");

    @Unique
    private Identifier getId(Config.CrossShape shape, Identifier customName) {
        return switch (shape) {
            case VANILLA -> CROSSHAIR_SPRITE;
            case ARROW -> ARROW_CROSS;
            case DOT -> DOT_CROSS;
            case CIRCLE -> CIRCLE_CROSS;
            case CUSTOM -> customName;
        };
    }
}
