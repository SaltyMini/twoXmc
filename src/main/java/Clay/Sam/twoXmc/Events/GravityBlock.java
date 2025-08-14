package Clay.Sam.twoXmc.Events;

import Clay.Sam.twoXmc.Cache;
import Clay.Sam.twoXmc.TwoXmc;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.Plugin;


public class GravityBlock implements Listener {

    private final Plugin plugin;
    private final Cache cache;

    public GravityBlock() {
        this.plugin = TwoXmc.getPlugin();
        this.cache = Cache.getInstance();
    }

    @EventHandler
    public void onEntityToBlock(EntityChangeBlockEvent event) {
        Location originalLoc = event.getBlock().getLocation();

        int blocksToUpdate = Cache.MAX_WORLD_HEIGHT - originalLoc.getBlockY();
        
        for(int i = 0; i < blocksToUpdate; i++) {
            cache.updateBitSetInCache(originalLoc.clone().add(0, i, 0));  // Use the correct location
        }
    }


}