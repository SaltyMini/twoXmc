package Clay.Sam.twoXmc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.sql.SQLException;
import java.util.BitSet;
import java.util.HashMap;

public final class TwoXmc extends JavaPlugin {

    private static String dbURL;
    private String dbPath;

    private BukkitTask minuteTask;

    private Cache cache;
    private SQLiteManager dbManager;
    private static Plugin plugin;

    @Override
    public void onEnable() {

        plugin = this;
        dbManager = SQLiteManager.getInstance();
        cache = Cache.getInstance();

        Cache.getBitSetCache();

        dbPath = getDataFolder().getAbsolutePath() + "/bitsets.db";
        dbURL = "jdbc:sqlite:" + dbPath;

        quickSaveCache();

    }

    @Override
    public void onDisable() {
        if (minuteTask != null) {
            minuteTask.cancel();
        }
        try {
            dbManager.quickSaveBitSets(cache.getBitSetCacheCopy());
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save BitSets to database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void quickSaveCache() {
        // 20 ticks = 1 second, so 1200 ticks = 60 seconds = 1 minute
        minuteTask = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                try {
                    dbManager.quickSaveBitSets(cache.getBitSetCacheCopy());
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to save BitSets to database: " + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }, 0L, 2400L); // 0L = no delay, 1200L = repeat every 1200 ticks (1 minute)
    }


    public static String getDbURL() {
        return dbURL;
    }

    public static Plugin getPlugin() {
        return plugin;
    }


}
