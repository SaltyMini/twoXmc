package Clay.Sam.twoXmc;

import org.bukkit.Location;

import java.util.BitSet;
import java.util.HashMap;

public class Cache {

    private static HashMap<String, BitSet> bitSetCache;
    private static Cache instance;

    // Constants for the new world height
    public static final int MIN_WORLD_HEIGHT = -64;
    public static final int MAX_WORLD_HEIGHT = 320;
    public static final int WORLD_HEIGHT_RANGE = MAX_WORLD_HEIGHT - MIN_WORLD_HEIGHT; // 384 blocks
    public static final int CHUNK_SIZE = 16;
    public static final int TOTAL_BLOCKS_PER_CHUNK = CHUNK_SIZE * CHUNK_SIZE * WORLD_HEIGHT_RANGE; // 98,304 blocks

    public Cache() {
        bitSetCache = new HashMap<>();
    }

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    public static HashMap<String, BitSet> getBitSetCache() {
        if (bitSetCache == null) {
            bitSetCache = new HashMap<>();
        }
        return bitSetCache;
    }

    public void putBitSetToCache(String id, BitSet bitSet) {
        bitSetCache.put(id, bitSet);
    }

    public BitSet getBitSetCacheEntry(Location chunkLoc) {
        return bitSetCache.get(formatChunkLocation(chunkLoc));
    }

    public BitSet getBitSetCacheEntry(String chunkLoc) {
        return bitSetCache.get(chunkLoc);
    }

    public static String formatChunkLocation(Location chunkLoc) {
        return chunkLoc.getWorld().getName() + "_" + chunkLoc.getBlockX() + "/" + chunkLoc.getBlockY() + "/" + chunkLoc.getBlockZ();
    }

    public void addBlockToCache(String chunkLoc, int bitSetIndex) {
        BitSet bitSet = getBitSetCacheEntry(chunkLoc);

        // Create a new bitset with the full chunk capacity
        if (bitSet == null) {
            bitSet = new BitSet(TOTAL_BLOCKS_PER_CHUNK + 1);
        }

        bitSet.set(bitSetIndex);
        putBitSetToCache(chunkLoc, bitSet);
    }

    public void removeBlockFromCache(Location chunkLoc, int bitSetIndex) {
        BitSet bitSet = getBitSetCacheEntry(formatChunkLocation(chunkLoc));

        if (bitSet != null) {
            bitSet.clear(bitSetIndex);
            putBitSetToCache(formatChunkLocation(chunkLoc), bitSet);
        }
    }

    public static int locToChunkRelativeIndex(Location loc) {
        // Get block coordinates relative to chunk (0-15 for X and Z)
        int chunkX = loc.getBlockX() & 15;
        int chunkZ = loc.getBlockZ() & 15;
        int y = loc.getBlockY();

        // Validate Y coordinate is within the valid range
        if (y < MIN_WORLD_HEIGHT || y > MAX_WORLD_HEIGHT) {
            throw new IllegalArgumentException("Y coordinate " + y + " is outside valid range [" + MIN_WORLD_HEIGHT + ", " + MAX_WORLD_HEIGHT + "]");
        }

        // Convert Y coordinate to relative index (0-383)
        int relativeY = y - MIN_WORLD_HEIGHT;

        // Convert 3D coordinates to 1D index
        // Formula: index = relativeY * 16 * 16 + z * 16 + x
        return relativeY * 256 + chunkZ * 16 + chunkX;
    }
}