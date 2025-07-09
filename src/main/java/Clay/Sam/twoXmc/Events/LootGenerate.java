package Clay.Sam.twoXmc.Events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class LootGenerate implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {

        if (event.getEntity() != null) return; // Skip if opened by entity

        List<ItemStack> originalLoot = new ArrayList<>(event.getLoot());
        event.getLoot().clear();

        for (ItemStack item : originalLoot) {
            if (item != null && item.getType() != Material.AIR) {
                // Add original item
                event.getLoot().add(item.clone());

                // Add doubled item
                ItemStack doubledLoot = item.clone();
                doubledLoot.setAmount(item.getAmount() * 2);
                event.getLoot().add(doubledLoot);
            }
        }
    }

}
