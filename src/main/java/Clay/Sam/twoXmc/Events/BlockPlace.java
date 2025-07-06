package Clay.Sam.twoXmc.Events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlace implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Location loc = event.getBlockPlaced().getLocation();
        Location chunkloc = loc.getChunk().getBlock(0, 64, 0).getLocation(); // Chunk origin at Y=64 X/Z=0

        // Get block coordinates relative to chunk (0-15 for X and Z)
        int chunkX = loc.getBlockX() & 15;
        int chunkZ = loc.getBlockZ() & 15;  // Equivalent to loc.getBlockZ() % 16
        int y = loc.getBlockY();

        // Convert 3D coordinates to 1D index
        // Formula: index = y * 16 * 16 + z * 16 + x
        int bitSetIndex = y * 256 + chunkZ * 16 + chunkX;

    }
}
