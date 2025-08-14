package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.BitSet;
import java.util.Collection;

public class BlockBreak implements Listener {

    private final Cache cache;

    public BlockBreak() {
        cache = Cache.getInstance();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws SQLException {
        Location loc = event.getBlock().getLocation();

        BitSet bitSet = cache.getBitSetCacheEntry(loc);

        Block block = event.getBlock();
        Collection<ItemStack> drops = block.getDrops(event.getPlayer().getInventory().getItemInMainHand());


        if(!bitSet.get(Cache.locToChunkRelativeIndex(loc))) {
            //bitset does not contain the block so x2
            for(ItemStack drop : drops) {
                int currentAmount = drop.getAmount();
                drop.setAmount(currentAmount * 2);
            }
        }

        //Drop the items
        Location dropLoc = loc.clone().add(0, 0.5, 0);
        event.setDropItems(false);
        for(ItemStack drop : drops) {
            dropLoc.getWorld().dropItemNaturally(dropLoc, drop);
        }

    }
}