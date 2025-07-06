package Clay.Sam.twoXmc;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class DebugTool implements Listener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {

        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GOLDEN_HOE) return;
        if(!(event.getPlayer().hasPermission("twoxmc.debugtool"))) return;


        Block block = event.getClickedBlock();
        if(block == null) return;

        //TODO: Check if block is placed by player using bitset

    }


}
