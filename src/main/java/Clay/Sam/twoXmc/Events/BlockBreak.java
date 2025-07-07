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
        Location chunkLoc = loc.getChunk().getBlock(0, 0, 0).getLocation(); // Chunk origin at X/Z=0, Y=minimum world height

        BitSet bitSet = cache.getBitSetCacheEntry(chunkLoc);

        if(bitSet == null) {
            return;
        }

        int blockBitSetIndex = Cache.locToChunkRelativeIndex(loc);

        if(bitSet.get(blockBitSetIndex)) {
            //Player placed block at location
            cache.removeBlockFromCache(chunkLoc, blockBitSetIndex);
        } else {
            event.setDropItems(false);
            ItemStack itemToDrop = (ItemStack) event.getBlock().getDrops();
            itemToDrop.setAmount(itemToDrop.getAmount() * 2); // Double the amount of dropped items
            loc.getWorld().dropItemNaturally(loc, itemToDrop);
        }

    }
}