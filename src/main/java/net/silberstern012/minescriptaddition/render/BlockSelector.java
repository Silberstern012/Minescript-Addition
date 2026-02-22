package net.silberstern012.minescriptaddition.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;

public class BlockSelector {

    private static final Map<BlockPos, float[]> SELECTED = new ConcurrentHashMap<>();

    // 🔥 RenderType ohne Depth-Test (für Through-Wall Glow)
    private static final RenderType NO_DEPTH_LINES = RenderType.create(
            "no_depth_lines",
            DefaultVertexFormat.POSITION_COLOR_NORMAL,
            VertexFormat.Mode.LINES,
            256,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderType.RENDERTYPE_LINES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(3.0))) // dickere Linie
                    .setTransparencyState(RenderType.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderType.NO_DEPTH_TEST) // 🔥 wichtig
                    .setCullState(RenderType.NO_CULL)
                    .setWriteMaskState(RenderType.COLOR_WRITE)
                    .createCompositeState(false)
    );

    public static void init() {
        NeoForge.EVENT_BUS.addListener(BlockSelector::onRenderLevel);
    }

    public static void select(BlockPos pos, float r, float g, float b) {
        SELECTED.put(pos, new float[]{r, g, b});
    }

    public static void unselect(BlockPos pos) {
        SELECTED.remove(pos);
    }

    public static void unselectAll() {
        SELECTED.clear();
    }

    private static void onRenderLevel(RenderLevelStageEvent event) {

        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS)
            return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        PoseStack poseStack = event.getPoseStack();
        var camera = event.getCamera();

        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        var bufferSource = mc.renderBuffers().bufferSource();

        for (Map.Entry<BlockPos, float[]> entry : SELECTED.entrySet()) {

            BlockPos pos = entry.getKey();
            float[] color = entry.getValue();

            AABB box = new AABB(pos).move(-camX, -camY, -camZ);

            // =========================
            // 1 Glow Layer (Through Walls)
            // =========================
            var glowBuffer = bufferSource.getBuffer(NO_DEPTH_LINES);

            LevelRenderer.renderLineBox(
                    poseStack,
                    glowBuffer,
                    box,
                    color[0],
                    color[1],
                    color[2],
                    0.35f // Transparency for Glow
            );

            // =========================
            // 2 Normal Line (with Depth)
            // =========================
            var normalBuffer = bufferSource.getBuffer(RenderType.lines());

            LevelRenderer.renderLineBox(
                    poseStack,
                    normalBuffer,
                    box,
                    color[0],
                    color[1],
                    color[2],
                    1.0f
            );
        }

        bufferSource.endBatch(NO_DEPTH_LINES);
        bufferSource.endBatch(RenderType.lines());
    }
}