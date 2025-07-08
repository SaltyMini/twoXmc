package Clay.Sam.twoXmc;

import Clay.Sam.twoXmc.Events.BlockPlace;
import Clay.Sam.twoXmc.Events.ChunkLoadUnload;
import Clay.Sam.twoXmc.Events.LootGenerate;
import Clay.Sam.twoXmc.Events.MobDrop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
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

        try {
            dbManager.connect();
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to database ");
            getLogger().severe("Failed to connect to database ");
            getLogger().severe("Failed to connect to database ");
            getLogger().severe("Failed to connect to database ");
            getLogger().severe("Failed to connect to database ");
            getLogger().severe("Error: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        quickSaveCache();
    }

    private void pluginManager() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockPlace(), plugin);
        pm.registerEvents(new ChunkLoadUnload(), plugin);
        pm.registerEvents(new LootGenerate(), plugin);
        pm.registerEvents(new MobDrop(), plugin);
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

        try {
            dbManager.quickSaveBitSets(cache.getBitSetCacheCopy());
            dbManager.disconnect(); // Fixed: Close database connection
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
