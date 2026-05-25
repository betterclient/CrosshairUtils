package io.github.betterclient.crosshairutils;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CrosshairUtils implements ClientModInitializer {
    //this has to be final since textures are created at startup
    public static final int CUSTOM_TEXTURE_SIZE = 15;

    public static ResourceLocation CUSTOM_MISS = ResourceLocation.tryBuild("crosshairutils", "hud/miss_crosshair");
    public static DynamicTexture missTexture;

    public static ResourceLocation CUSTOM_ENTITY = ResourceLocation.tryBuild("crosshairutils", "hud/entity_crosshair");
    public static DynamicTexture entityTexture;

    public static ResourceLocation CUSTOM_BLOCK = ResourceLocation.tryBuild("crosshairutils", "hud/block_crosshair");
    public static DynamicTexture blockTexture;

    public static final Logger LOGGER = LoggerFactory.getLogger("CrosshairUtils");

    public static final List<ResourceLocation> customTextures = List.of(CUSTOM_MISS, CUSTOM_ENTITY, CUSTOM_BLOCK);

    public static void load(int[][] customShape, DynamicTexture texture) {
        NativeImage nativeImage = texture.getPixels();
        for (int x = 0; x < CUSTOM_TEXTURE_SIZE; x++) {
            for (int y = 0; y < CUSTOM_TEXTURE_SIZE; y++) {
                if (x >= customShape.length || y >= customShape[x].length) continue; //custom texture size got changed...
                assert nativeImage != null;
                int color = customShape[x][y];
                nativeImage.setPixelRGBA(x, y, argbToAbgr(color));
            }
        }
        texture.upload();
    }

    public static int argbToAbgr(int argb) {
        int a = (argb >> 24) & 0xFF;
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;

        return (a << 24) | (b << 16) | (g << 8) | r;
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("CrosshairUtils initialized");
    }
}
