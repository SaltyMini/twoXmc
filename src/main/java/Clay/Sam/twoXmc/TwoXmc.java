package Clay.Sam.twoXmc;

import org.bukkit.plugin.java.JavaPlugin;

public final class TwoXmc extends JavaPlugin {

    private static String dbURL;
    private String dbPath;

    @Override
    public void onEnable() {
        // Plugin startup logic

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
}
