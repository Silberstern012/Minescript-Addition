package net.silberstern012.minescriptaddition.scoreboard;

import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.PlayerScoreEntry;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardUtils {

    private static final Gson GSON = new Gson();

    //Returns Sidebar-Scores as Map

    public static Map<String, Integer> getSidebarScores() {
        Minecraft mc = Minecraft.getInstance();
        Map<String, Integer> result = new HashMap<>();

        if (mc.level == null) return result;

        Scoreboard scoreboard = mc.level.getScoreboard();
        Objective objective = scoreboard.getDisplayObjective(DisplaySlot.SIDEBAR);

        if (objective == null) return result;

        for (PlayerScoreEntry entry : scoreboard.listPlayerScores(objective)) {
            result.put(entry.owner(), entry.value());
        }

        return result;
    }

    //Returns Scoreboard as Json

    public static String getSidebarScoresJson() {
        return GSON.toJson(getSidebarScores());
    }
}