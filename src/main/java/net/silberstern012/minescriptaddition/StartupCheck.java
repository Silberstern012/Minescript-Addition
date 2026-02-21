package net.silberstern012.minescriptaddition;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.io.IOException;
import java.net.URI;
import java.io.InputStream;

//public static final Logger LOGGER = LogUtils.getLogger();

public class StartupCheck {
    private static final String SCRIPT_URL = "https://raw.githubusercontent.com/Silberstern012/Minescript-Addition/refs/heads/neoforge/extension.py";
    private static final String FILE_NAME = "extension.py";

    public static void checkAndDownload() {
        Path targetPath = Paths.get("minescript", FILE_NAME);
        if (!Files.exists(targetPath)) {
            // Run on a separate thread to avoid hanging the game during startup
            new Thread(() -> {
                try {
                    Files.createDirectories(targetPath.getParent());
                    try (InputStream in = URI.create(SCRIPT_URL).toURL().openStream()) {
                        Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Downloaded Addition scripts");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}