package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PistonPlace implements Listener {

    private final Cache cache;

    public PistonPlace() {
        cache = Cache.getInstance();
    }

    @EventHandler
    public void onPistonPlace(BlockPlaceEvent event) {

        if(event.getBlock().getType().name().contains("PISTON") || event.getBlock().getType() == Material.SLIME_BLOCK || event.getBlock().getType() == Material.HONEY_BLOCK) {

            //Set bits for blocks around the piston to player placed
            Block pistonBlock = event.getBlock();

            // Get all adjacent blocks (6 directions)
            BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH,
                    BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

            for(BlockFace face : faces) {
                Block adjacentBlock = pistonBlock.getRelative(face);

                cache.updateBitSetInCache(adjacentBlock.getLocation());

            }
        }


    }
}
