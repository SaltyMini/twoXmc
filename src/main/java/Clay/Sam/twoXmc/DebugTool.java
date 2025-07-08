package Clay.Sam.twoXmc;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.BitSet;

public class DebugTool implements Listener {

    private Cache cache;

    public DebugTool() {
        cache = cache.getInstance();
    }

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {

        if(event.getPlayer().getInventory().getItemInMainHand().getType() != Material.GOLDEN_HOE) return;
        if(!(event.getPlayer().hasPermission("twoxmc.debugtool"))) return;


        Block block = event.getClickedBlock();

        Player player = event.getPlayer();

        player.sendMessage("Clicked Block: " + block.getType()+" at cords: " + block.getLocation().toVector());
        player.sendMessage("Getting Block Data and bit set. BitSet: " + Cache.formatChunkLocation(block.getLocation()));

        BitSet bitset = cache.getBitSetCacheEntry(block.getLocation());

        int amountOfPlayerBlocksInchunk = 0;

        if(bitset != null) {
            for(int i = 0; i < bitset.length(); i++) {
                if(bitset.get(i)) {
                    amountOfPlayerBlocksInchunk++;
                }
            }
        }

        int bitsetIndex = Cache.locToChunkRelativeIndex(block.getLocation());

        player.sendMessage("Amount of Player Placed Blocks in Chunk: " + amountOfPlayerBlocksInchunk);
        player.sendMessage("BitSet Index: " + bitsetIndex);
        player.sendMessage("Is Block Placed by Player: " + (bitset != null && bitset.get(bitsetIndex)));

    }


}
