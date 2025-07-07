package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import Clay.Sam.twoXmc.TwoXmc;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.BitSet;

public class BlockPlace implements Listener {

    private final Cache cache;

    public BlockPlace() {
        cache = Cache.getInstance();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Location loc = event.getBlockPlaced().getLocation();
        Location chunkLoc = loc.getChunk().getBlock(0, 0, 0).getLocation(); // Chunk origin at Y=64 X/Z=0

        String setName = Cache.formatChunkLocation(chunkLoc);

        cache.addBlockToCache(setName, Cache.locToChunkRelativeIndex(loc));
    }
}
