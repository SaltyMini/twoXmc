package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import Clay.Sam.twoXmc.SQLiteManager;
import Clay.Sam.twoXmc.TwoXmc;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.BitSet;

public class ChunkLoadUnload implements Listener {

    private final Cache cache;
    private final SQLiteManager dbManager;
    private final Plugin plugin;

    public ChunkLoadUnload() {
       cache = Cache.getInstance();
         dbManager = SQLiteManager.getInstance();
        plugin = TwoXmc.getPlugin();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) throws SQLException {

        Location loc = event.getChunk().getBlock(0, 0, 0).getLocation();
        plugin.getLogger().info("Chunk loaded at: " + Cache.formatChunkLocation(event.getChunk().getBlock(0, 0, 0).getLocation()));

        if(cache.getBitSetCacheEntry(loc) == null) {;
            BitSet bitset = dbManager.getBitSet(Cache.formatChunkLocation(loc));
            cache.putBitSetToCache(loc, bitset);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) throws SQLException {
        plugin.getLogger().info("Chunk unloaded at: " + Cache.formatChunkLocation(event.getChunk().getBlock(0, 0, 0).getLocation()));
        Location loc = event.getChunk().getBlock(0, 0, 0).getLocation();

        // Remove the BitSet from the cache when the chunk unloads
        if (cache.getBitSetCacheEntry(loc) != null) {
            dbManager.setBitSet(Cache.formatChunkLocation(loc), cache.getBitSetCacheEntry(loc));
            cache.removeBitSetFromCache(loc);
        }
    }

}
