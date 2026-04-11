package io.github.betterclient.crosshairutils.mixin;

import io.github.betterclient.crosshairutils.config.Config;
import io.github.betterclient.crosshairutils.config.ConfigSerializer;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "stop", at = @At("HEAD"))
    private void stop(CallbackInfo ci) {
        ConfigSerializer.saveConfig(Config.getInstance());
    }
}
