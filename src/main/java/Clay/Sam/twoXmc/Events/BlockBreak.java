package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.BitSet;

public class BlockBreak implements Listener {

    private final Cache cache;

    public BlockBreak() {
        cache = Cache.getInstance();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) throws SQLException {
        Location loc = event.getBlock().getLocation();

        cache.updateBitSetInCache(loc, Cache.BitSetAction.REMOVE);
    }
}