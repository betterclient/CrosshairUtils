package io.github.betterclient.crosshairutils.mixin;

import io.github.betterclient.crosshairutils.CrosshairUtils;
import io.github.betterclient.crosshairutils.config.Config;
import io.github.betterclient.crosshairutils.config.CrosshairMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.betterclient.crosshairutils.CrosshairUtils.CUSTOM_TEXTURE_SIZE;

@Mixin(LoadingOverlay.class)
public class MixinSplashOverlay {
    @Inject(method = "registerTextures", at = @At("HEAD"))
    private static void registerTextures(TextureManager textureManager, CallbackInfo ci) {
        Minecraft.getInstance().getTextureManager().register(
                CrosshairUtils.CUSTOM_MISS,
                CrosshairUtils.missTexture = new DynamicTexture(CUSTOM_TEXTURE_SIZE, CUSTOM_TEXTURE_SIZE, false)
        );
        CrosshairUtils.load(Config.getInstance().getCustomShape(CrosshairMode.NORMAL), CrosshairUtils.missTexture);

        Minecraft.getInstance().getTextureManager().register(
                CrosshairUtils.CUSTOM_ENTITY,
                CrosshairUtils.entityTexture = new DynamicTexture(CUSTOM_TEXTURE_SIZE, CUSTOM_TEXTURE_SIZE, false)
        );
        CrosshairUtils.load(Config.getInstance().getCustomShape(CrosshairMode.ATTACK_ENTITY), CrosshairUtils.entityTexture);

        Minecraft.getInstance().getTextureManager().register(
                CrosshairUtils.CUSTOM_BLOCK,
                CrosshairUtils.blockTexture = new DynamicTexture(CUSTOM_TEXTURE_SIZE, CUSTOM_TEXTURE_SIZE, false)
        );
        CrosshairUtils.load(Config.getInstance().getCustomShape(CrosshairMode.ATTACK_BLOCK), CrosshairUtils.blockTexture);
    }
}
