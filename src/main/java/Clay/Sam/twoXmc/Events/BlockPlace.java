package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import Clay.Sam.twoXmc.TwoXmc;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.SQLException;

public class BlockPlace implements Listener {

    private final Cache cache;

    public BlockPlace() {
        cache = Cache.getInstance();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) throws SQLException {

        Location loc = event.getBlockPlaced().getLocation();

        cache.updateBitSetInCache(loc);
    }
}