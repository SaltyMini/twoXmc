package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import Clay.Sam.twoXmc.TwoXmc;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.BitSet;

public class BlockPlace implements Listener {

    private Cache cache;

    public BlockPlace() {
        Cache.getInstance();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Location loc = event.getBlockPlaced().getLocation();
        Location chunkLoc = loc.getChunk().getBlock(0, 64, 0).getLocation(); // Chunk origin at Y=64 X/Z=0

        String setName = Cache.formatChunkLocation(chunkLoc);

        // Get block coordinates relative to chunk (0-15 for X and Z)
        int chunkX = loc.getBlockX() & 15;
        int chunkZ = loc.getBlockZ() & 15;  // Equivalent to loc.getBlockZ() % 16
        int y = loc.getBlockY();

        // Convert 3D coordinates to 1D index
        // Formula: index = y * 16 * 16 + z * 16 + x
        int bitSetIndex = y * 256 + chunkZ * 16 + chunkX;

        cache.addBlockToCache(setName, bitSetIndex);

    }
}
