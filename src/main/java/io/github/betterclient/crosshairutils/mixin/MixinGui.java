package io.github.betterclient.crosshairutils.mixin;

import com.llamalad7.mixinextras.expression.Expression;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.betterclient.crosshairutils.CrosshairUtils;
import io.github.betterclient.crosshairutils.config.Config;
import io.github.betterclient.crosshairutils.config.CrossShape;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;
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
    private static ResourceLocation CROSSHAIR_SPRITE;
    @Unique
    private static final Config config = Config.getInstance();

    @Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
    public void onRenderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (!config.shouldRenderCrosshair()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/Gui;CROSSHAIR_SPRITE:Lnet/minecraft/resources/ResourceLocation;", opcode = Opcodes.GETSTATIC))
    public ResourceLocation redirectCrosshairSprite() {
        Minecraft mc = Minecraft.getInstance();
        return switch (mc.hitResult.getType()) {
            case MISS -> getId(config.getShape(), CrosshairUtils.CUSTOM_MISS);
            case BLOCK -> getId(config.getShapeAttackBlock(), CrosshairUtils.CUSTOM_BLOCK);
            case ENTITY -> getId(config.getShapeAttackEntity(), CrosshairUtils.CUSTOM_ENTITY);
        };
    }

    @Unique
    private static final ResourceLocation ARROW_CROSS = ResourceLocation.tryBuild("crosshairutils", "hud/arrow_crosshair");
    @Unique
    private static final ResourceLocation DOT_CROSS = ResourceLocation.tryBuild("crosshairutils", "hud/dot_crosshair");
    @Unique
    private static final ResourceLocation CIRCLE_CROSS = ResourceLocation.tryBuild("crosshairutils", "hud/circle_crosshair");

    @Unique
    private ResourceLocation getId(CrossShape shape, ResourceLocation customName) {
        return switch (shape) {
            case VANILLA -> CROSSHAIR_SPRITE;
            case ARROW -> ARROW_CROSS;
            case DOT -> DOT_CROSS;
            case CIRCLE -> CIRCLE_CROSS;
            case CUSTOM -> customName;
        };
    }
}