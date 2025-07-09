package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.BitSet;

public class BlockBreak implements Listener {

    private final Cache cache;

    public BlockBreak() {
        cache = Cache.getInstance();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        Location chunkLoc = loc.getChunk().getBlock(0, 0, 0).getLocation();

        BitSet bitSet = cache.getBitSetCacheEntry(chunkLoc);

        int blockBitSetIndex = Cache.locToChunkRelativeIndex(loc);

        if(bitSet.get(blockBitSetIndex)) {
            //Player placed block at location
            cache.removeBlockFromCache(chunkLoc, blockBitSetIndex);
        } else {
            event.setDropItems(false);
            for (ItemStack drop : event.getBlock().getDrops()) {
                //TODO: Implement fortune support
                ItemStack itemToDrop = drop.clone();
                itemToDrop.setAmount(itemToDrop.getAmount() * 2); // Double the number of dropped items
                loc.getWorld().dropItemNaturally(loc, itemToDrop);
            }
        }
    }
}