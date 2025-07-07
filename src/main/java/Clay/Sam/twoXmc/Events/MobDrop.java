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
        event.getDrops().clear(); // Clear the default drops

        Location loc = event.getEntity().getLocation();

        for(ItemStack drop : drops) {
            ItemStack doubledDrop = drop.clone();
            doubledDrop.setAmount(drop.getAmount() * 2);
            loc.getWorld().dropItemNaturally(loc, doubledDrop);
        }

    }

}
