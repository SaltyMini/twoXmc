package Clay.Sam.twoXmc.Events;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MobDrop implements Listener {

    @EventHandler
    public void onMobDrop(EntityDeathEvent event) {

        List<ItemStack> drops = event.getDrops();

        event.setDroppedExp((int) (event.getDroppedExp() * 1.5)); // double exp

        Location loc = event.getEntity().getLocation();

        for(ItemStack drop : drops) {
            ItemStack doubledDrop = drop.clone(); //Drop another copy of the item
            loc.getWorld().dropItemNaturally(loc, doubledDrop);
        }

    }

}
