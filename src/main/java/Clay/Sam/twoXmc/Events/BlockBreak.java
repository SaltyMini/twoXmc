package Clay.Sam.twoXmc.Events;

import org.bukkit.Location;
import org.bukkit.event.Listener;

public class BlockBreak implements Listener {



    /**
     * Decode a 1D bitset index back to world block coordinates
     * @param bitSetIndex The 1D index from the bitset
     * @param chunkLoc The chunk origin location (from chunk.getBlock(0, 64, 0))
     * @return Location object with world coordinates
     */
    private Location decodeBitSetIndex(int bitSetIndex, Location chunkLoc) {

        // The remainder when divided by 16 gives the X position within the current row
        int chunkRelativeX = bitSetIndex % 16;

        // Divide by 16 to remove the X component, then mod 16 to get Z within the current Y level
        // This extracts the Z position from the "z * 16" part of our original formula
        int chunkRelativeZ = (bitSetIndex / 16) % 16;

        // Get Y coordinate (absolute world Y coordinate)
        // Divide by 256 to remove both X and Z components (since each Y level has 16*16=256 blocks)
        // This extracts the Y position from the "y * 256" part of our original formula
        int worldY = bitSetIndex / 256;

        // Convert chunk-relative X and Z coordinates to world coordinates
        // Since chunkLoc is the location of block (0,64,0) in the chunk:
        // - chunkLoc.getBlockX() gives the world X coordinate of chunk position 0
        // - chunkLoc.getBlockZ() gives the world Z coordinate of chunk position 0
        // add the chunk-relative coordinates to get absolute world position
        int worldX = chunkLoc.getBlockX() + chunkRelativeX;
        int worldZ = chunkLoc.getBlockZ() + chunkRelativeZ;

        return new Location(chunkLoc.getWorld(), worldX, worldY, worldZ);
    }


}
