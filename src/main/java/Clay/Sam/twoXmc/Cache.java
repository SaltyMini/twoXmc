package Clay.Sam.twoXmc;

import org.bukkit.Location;

import java.util.BitSet;
import java.util.HashMap;

public class Cache {

    private static HashMap<String, BitSet> bitSetCache;
    private static Cache instance;

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

        //Create a new bitset at the length of the block index
        if (bitSet == null) {
            bitSet = new BitSet(bitSetIndex + 1);
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
        int chunkZ = loc.getBlockZ() & 15;  // Equivalent to loc.getBlockZ() % 16
        int y = loc.getBlockY();

        // Convert 3D coordinates to 1D index
        // Formula: index = y * 16 * 16 + z * 16 + x
        return y * 256 + chunkZ * 16 + chunkX;
    }

}
