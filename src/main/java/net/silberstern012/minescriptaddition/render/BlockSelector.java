package net.silberstern012.minescriptaddition.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockSelector {

    private static final Map<BlockPos, float[]> SELECTED = new ConcurrentHashMap<>();

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
        var buffer = bufferSource.getBuffer(RenderType.lines());

        for (Map.Entry<BlockPos, float[]> entry : SELECTED.entrySet()) {

            BlockPos pos = entry.getKey();
            float[] color = entry.getValue();

            AABB box = new AABB(pos).move(-camX, -camY, -camZ);

            LevelRenderer.renderLineBox(
                    poseStack,
                    buffer,
                    box,
                    color[0],
                    color[1],
                    color[2],
                    1.0f
            );
        }
        bufferSource.endBatch(RenderType.lines());
    }
}