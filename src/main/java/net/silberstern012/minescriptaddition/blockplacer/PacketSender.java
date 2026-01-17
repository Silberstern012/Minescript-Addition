package net.silberstern012.minescriptaddition.blockplacer;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;



public class PacketSender {

    public static void placeBlockAt(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getConnection() == null) return;

        BlockPos target = pos;

        if (!mc.level.getBlockState(target).canBeReplaced()) {
            return;
        }

        if (mc.level.getBlockState(target.below()).isAir()) {
            target = target.above();
        }

        BlockHitResult hit = new BlockHitResult(
                Vec3.atCenterOf(target.below()),
                Direction.UP,
                target.below(),
                false
        );

        ServerboundUseItemOnPacket packet =
                new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hit, 0);

        // Optional: prüfen ob Spieler überhaupt einen Block hält
        //ItemStack stack = mc.player.getMainHandItem();
        //if ((!(mc.player.getMainHandItem().getItem() instanceof Blockitem)) && !mc.player.getMainHandItem().isEmpty()) {

        if (!mc.player.getMainHandItem().isEmpty()) {
            //&& mc.player.getMainHandItem().is(Items.DIRT)) {

            mc.getConnection().send(packet);
            mc.player.displayClientMessage(
                    Component.literal("Placed block at " + pos.toShortString()), true);
        }
    }
    public static void breakBlockAt(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getConnection() == null) return;

        // "Start breaking"
        ServerboundPlayerActionPacket start = new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK,
                pos,
                Direction.UP
        );

        // "Stop breaking" (macht es instant wie Creative / Nuker)
        ServerboundPlayerActionPacket stop = new ServerboundPlayerActionPacket(
                ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK,
                pos,
                Direction.UP
        );

        mc.getConnection().send(start);
        mc.getConnection().send(stop);

        mc.player.displayClientMessage(
                Component.literal("Broke block at " + pos.toShortString()), true);
    }
}
