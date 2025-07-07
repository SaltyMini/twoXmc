package Clay.Sam.twoXmc;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.BitSet;
import java.util.HashMap;

public final class TwoXmc extends JavaPlugin {

    private static String dbURL;
    private String dbPath;


    private Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        Cache.getBitSetCache();

        dbPath = getDataFolder().getAbsolutePath() + "/bitsets.db";
        dbURL = "jdbc:sqlite:" + dbPath;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static String getDbURL() {
        return dbURL;
    }

    public Plugin getPlugin() {
        return plugin;
    }


}
