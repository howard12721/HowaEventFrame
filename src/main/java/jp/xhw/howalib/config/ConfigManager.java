package jp.xhw.howalib.config;

import jp.xhw.howalib.HowaLib;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.logging.Level;

@SuppressWarnings("ResultOfMethodCallIgnored")
public abstract class ConfigManager {

    @Getter
    private String fileName = "config.yml";
    @Getter
    private String resourceName = "config.yml";

    @Getter
    @Nullable
    private FileConfiguration config = null;

    public ConfigManager(String fileName, String resourceName) {
        this.fileName = fileName;
        this.resourceName = resourceName;
    }

    public boolean load() {
        Plugin plugin = HowaLib.getInstance().getPlugin();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), fileName);

        try {
            if (!file.exists()) {
                try (InputStream inputStream = plugin.getResource(resourceName);
                     OutputStream outputStream = new FileOutputStream(file)) {
                    if (inputStream == null) {
                        file.createNewFile();
                    } else {
                        inputStream.transferTo(outputStream);
                    }
                }
            }

            config = YamlConfiguration.loadConfiguration(file);

        } catch (IOException e) {
            HowaLib.getInstance().getLogger().log(Level.SEVERE, fileName + "のロードに失敗しました", e);
            return false;
        }

        return true;
    }

    public boolean save() {
        Plugin plugin = HowaLib.getInstance().getPlugin();

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        File file = new File(plugin.getDataFolder(), fileName);

        if (config == null) return false;

        try {
            config.save(file);
        } catch (IOException e) {
            HowaLib.getInstance().getLogger().log(Level.SEVERE, fileName + "のセーブに失敗しました", e);
            return false;
        }

        return true;
    }

}
