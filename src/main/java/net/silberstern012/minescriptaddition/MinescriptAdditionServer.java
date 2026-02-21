package net.silberstern012.minescriptaddition;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.silberstern012.minescriptaddition.scoreboard.ScoreboardUtils;
import net.silberstern012.minescriptaddition.scoreboard.ScoreboardAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;



public class MinescriptAdditionServer implements Runnable {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(25566)) {

            while (true) {
                try (Socket client = serverSocket.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(client.getInputStream()));
                     PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {

                    String msg = in.readLine();
                    if (msg == null || msg.trim().isEmpty()) continue;

                    msg = msg.trim();
                    String[] args = msg.split(" ");

                    //System.out.println(Arrays.toString(args)); //Some errors because Arrays.

                    // ===== GET SCOREBOARD (SYNC, NOT ASYNC!) =====
                    if (msg.equalsIgnoreCase("getscoreboard")) {
                        String json = ScoreboardUtils.getSidebarScoresJson();
                        out.println(json);
                        //System.out.println("[MinescriptAddition] Sent scoreboard: " + json); //DEBUG ONLY
                        continue;
                    }

                    if (msg.equalsIgnoreCase("getscoreboards")) {
                        out.println(ScoreboardAPI.getAllScoreboardsJson());
                        continue;
                    }

                    if (msg.startsWith("getscoreboard ")) {
                        String slot = msg.split(" ")[1];
                        out.println(ScoreboardAPI.getScoreboardForSlotJson(slot));
                        continue;
                    }

                    if (msg.equalsIgnoreCase("getbossbars")) {
                        out.println(ScoreboardAPI.getBossbarsJson());
                        continue;
                    }

                    // ===== BLOCK COMMANDS =====
                    if (args.length == 4) {
                        int x = Integer.parseInt(args[1]);
                        int y = Integer.parseInt(args[2]);
                        int z = Integer.parseInt(args[3]);
                        BlockPos pos = new BlockPos(x, y, z);

                        Minecraft.getInstance().execute(() -> {

                            if (args[0].equalsIgnoreCase("placeblock")) {
                                PacketSender.placeBlockAt(pos);
                            }

                            if (args[0].equalsIgnoreCase("placeoffblock")) {
                                PacketSender.placeOffBlockAt(pos);
                            }

                            if (args[0].equalsIgnoreCase("breakblock")) {
                                PacketSender.breakBlockAt(pos);
                            }

                        });
                    }

                } catch (Exception e) {
                    System.out.println("[MinescriptAddition] Client error: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("[MinescriptAddition] Server error: " + e.getMessage());
        }
    }
}