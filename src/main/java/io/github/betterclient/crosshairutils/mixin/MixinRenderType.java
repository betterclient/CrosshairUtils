package io.github.betterclient.crosshairutils.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.betterclient.crosshairutils.config.Config;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.*;

import java.util.function.Function;

@Mixin(RenderType.class)
public abstract class MixinRenderType {
    @Shadow
    public static RenderType.CompositeRenderType create(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, RenderType.CompositeState compositeState) {
        return null;
    }

    @Shadow
    @Final
    private static Function<ResourceLocation, RenderType> CROSSHAIR;

    /**
     * @author betterclient
     * @reason override crosshair
     */
    @Overwrite
    public static RenderType crosshair(ResourceLocation location) {
        return switch (Config.getInstance().getCrosshairBlendMode()) {
            case NORMAL -> NORMAL_CROSSHAIR.apply(location);
            case ADDITIVE -> ADDITIVE_CROSSHAIR.apply(location);
            case INVERT -> CROSSHAIR.apply(location);
        };
    }

    @Unique
    private static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });

    private static final Function<ResourceLocation, RenderType> ADDITIVE_CROSSHAIR = Util.memoize(
            (resourceLocation) ->
                    create(
                            "additive_crosshair",
                            DefaultVertexFormat.POSITION_TEX_COLOR,
                            VertexFormat.Mode.QUADS,
                            786432,
                            RenderType.CompositeState
                                    .builder()
                                    .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false))
                                    .setShaderState(RenderStateShard.POSITION_TEXTURE_COLOR_SHADER)
                                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                                    .createCompositeState(false)
                    )
    );

    private static final Function<ResourceLocation, RenderType> NORMAL_CROSSHAIR = Util.memoize(
            (resourceLocation) ->
                    create(
                            "normal_crosshair",
                            DefaultVertexFormat.POSITION_TEX_COLOR,
                            VertexFormat.Mode.QUADS,
                            786432,
                            RenderType.CompositeState
                                    .builder()
                                    .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, TriState.FALSE, false))
                                    .setShaderState(RenderStateShard.POSITION_TEXTURE_COLOR_SHADER)
                                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
                                    .createCompositeState(false)
                    )
    );
}
