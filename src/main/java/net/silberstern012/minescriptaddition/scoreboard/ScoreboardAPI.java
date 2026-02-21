package net.silberstern012.minescriptaddition.scoreboard;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;

import java.util.*;

public class ScoreboardAPI {

    private static final Gson GSON = new Gson();

    // =========================
    // ALL DISPLAY SLOTS
    // =========================
    public static String getAllScoreboardsJson() {
        Map<String, Object> result = new HashMap<>();

        result.put("sidebar", getScoresForSlot(DisplaySlot.SIDEBAR));
        result.put("list", getScoresForSlot(DisplaySlot.LIST));
        result.put("belowname", getScoresForSlot(DisplaySlot.BELOW_NAME));

        return GSON.toJson(result);
    }

    public static String getScoreboardForSlotJson(String slotName) {
        DisplaySlot slot = switch (slotName.toLowerCase()) {
            case "sidebar" -> DisplaySlot.SIDEBAR;
            case "list" -> DisplaySlot.LIST;
            case "belowname" -> DisplaySlot.BELOW_NAME;
            default -> null;
        };

        if (slot == null) return "{}";

        return GSON.toJson(getScoresForSlot(slot));
    }

    private static Map<String, Integer> getScoresForSlot(DisplaySlot slot) {
        Minecraft mc = Minecraft.getInstance();
        Map<String, Integer> result = new HashMap<>();

        if (mc.level == null) return result;

        Scoreboard scoreboard = mc.level.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(slot);

        if (objective == null) return result;

        for (PlayerScoreEntry entry : scoreboard.listPlayerScores(objective)) {
            result.put(entry.owner(), entry.value());
        }

        return result;
    }

    // =========================
    // BOSSBARS
    // =========================
    public static String getBossbarsJson() {
        Minecraft mc = Minecraft.getInstance();
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> bossbars = new ArrayList<>();

        if (mc.gui == null) return "{}";

        try {
            BossHealthOverlay overlay = mc.gui.getBossOverlay();

            // Zugriff auf privates Feld "events"
            var field = BossHealthOverlay.class.getDeclaredField("events");
            field.setAccessible(true);

            Map<?, ?> events = (Map<?, ?>) field.get(overlay);

            for (Object obj : events.values()) {
                if (obj instanceof LerpingBossEvent event) {

                    Map<String, Object> boss = new HashMap<>();
                    boss.put("name", event.getName().getString());
                    boss.put("progress", event.getProgress());
                    boss.put("color", event.getColor().name());
                    boss.put("overlay", event.getOverlay().name());

                    bossbars.add(boss);
                }
            }

        } catch (Exception e) {
            return "{}";
        }

        result.put("bossbars", bossbars);
        return GSON.toJson(result);
    }
}