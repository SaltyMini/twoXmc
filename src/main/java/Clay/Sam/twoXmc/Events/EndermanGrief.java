package Clay.Sam.twoXmc.Events;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EndermanGrief implements Listener {

    @EventHandler
    public void onEndermanGrief(EntityChangeBlockEvent event) {

        if(event.getEntityType().equals(EntityType.ENDERMAN)) {
            event.setCancelled(true);
        //TODO: Prevent endermen picking up player placed blocks
            //Current disable all grief
        }

    }
}
