package jp.xhw.howalib;

import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class HowaLib {

    @Getter
    private static HowaLib instance;

    public static void onEnable(Plugin plugin) {
        if (instance != null) {
            throw new IllegalStateException("HowaLib is already in use.");
        }

        instance = new HowaLib(plugin);
    }

    public static void onDisable() {

    }

    @Getter
    private final Plugin plugin;
    @Getter
    private final Logger logger;

    private HowaLib(Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

}