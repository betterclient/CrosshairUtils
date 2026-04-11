package io.github.betterclient.crosshairutils;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CrosshairUtils implements ClientModInitializer {
    //this has to be final since textures are created at startup
    public static final int CUSTOM_TEXTURE_SIZE = 15;

    public static Identifier CUSTOM_MISS = Identifier.tryBuild("crosshairutils", "hud/miss_crosshair");
    public static DynamicTexture missTexture;

    public static Identifier CUSTOM_ENTITY = Identifier.tryBuild("crosshairutils", "hud/entity_crosshair");
    public static DynamicTexture entityTexture;

    public static Identifier CUSTOM_BLOCK = Identifier.tryBuild("crosshairutils", "hud/block_crosshair");
    public static DynamicTexture blockTexture;

    public static final Logger LOGGER = LoggerFactory.getLogger("CrosshairUtils");

    public static final List<Identifier> customTextures = List.of(CUSTOM_MISS, CUSTOM_ENTITY, CUSTOM_BLOCK);

    public static void load(int[][] customShape, DynamicTexture texture) {
        NativeImage nativeImage = texture.getPixels();
        for (int x = 0; x < CUSTOM_TEXTURE_SIZE; x++) {
            for (int y = 0; y < CUSTOM_TEXTURE_SIZE; y++) {
                if (x >= customShape.length || y >= customShape[x].length) continue; //custom texture size got changed...
                assert nativeImage != null;
                nativeImage.setPixel(x, y, customShape[x][y]);
            }
        }
        texture.upload();
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("CrosshairUtils initialized");
    }
}
