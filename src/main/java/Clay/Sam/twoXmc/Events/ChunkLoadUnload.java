package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.BitSet;

public class ChunkLoadUnload implements Listener {

    private Cache cache;

    public void ChunkLoadUnload() {
       cache = Cache.getInstance();
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {

        Location loc = event.getChunk().getBlock(0, 0, 0).getLocation();


        if(cache.getBitSetCacheEntry(loc) == null) {;
            BitSet bitset = null; //TODO: Get from DB
            cache.putBitSetToCache(loc, bitset);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkLoadEvent event) {
        Location loc = event.getChunk().getBlock(0, 0, 0).getLocation();

        // Remove the BitSet from the cache when the chunk unloads
        if (cache.getBitSetCacheEntry(loc) != null) {
            //TODO: Save bitset to DB
            cache.removeBitSetFromCache(loc);
        }
    }

}
