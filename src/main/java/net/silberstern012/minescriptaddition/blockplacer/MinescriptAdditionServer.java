package net.silberstern012.minescriptaddition.blockplacer;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MinescriptAdditionServer implements Runnable {

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(25566)) {

            while (true) {
                try (Socket client = serverSocket.accept();
                     BufferedReader in = new BufferedReader(
                             new InputStreamReader(client.getInputStream()))) {

                    String msg = in.readLine();
                    if (msg == null) continue;

                    String[] args = msg.split(" ");

                    if (args.length == 4 && args[0].equalsIgnoreCase("placeblock")) {
                        int x = Integer.parseInt(args[1]);
                        int y = Integer.parseInt(args[2]);
                        int z = Integer.parseInt(args[3]);

                        // Muss im Minecraft-Thread laufen
                        Minecraft.getInstance().execute(() -> {
                            PacketSender.placeBlockAt(new BlockPos(x, y, z));
                        });
                        System.out.println("Placeblock");
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