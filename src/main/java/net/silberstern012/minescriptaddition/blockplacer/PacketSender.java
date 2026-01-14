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

public class PacketSender {

    public static void placeBlockAt(BlockPos pos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.getConnection() == null) return;

        // Wir "klicken" fake auf den Block UNTER der Zielposition
        BlockHitResult hit = new BlockHitResult(
                Vec3.atCenterOf(pos.below()),
                Direction.UP,
                pos.below(),
                false
        );

        ServerboundUseItemOnPacket packet =
                new ServerboundUseItemOnPacket(InteractionHand.MAIN_HAND, hit, 0);

        // Optional: prüfen ob Spieler überhaupt einen Block hält
        if (!mc.player.getMainHandItem().isEmpty()
                && mc.player.getMainHandItem().is(Items.DIRT)) {

            mc.getConnection().send(packet);
            mc.player.displayClientMessage(
                    Component.literal("Placed block at " + pos.toShortString()), true);
        }
    }
}
