package Clay.Sam.twoXmc;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
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

    /**
     * Puts a BitSet into the cache based on the chunk location.
     * @param loc the chunk location
     * @param bitSet the BitSet to cache
     */
    public void putBitSetToCache(Location loc, BitSet bitSet) {
        bitSetCache.put(formatChunkLocation(loc), bitSet);
    }

    /**
     * Puts a BitSet into the cache based on the chunk location as a string.
     * @param loc formatted chunk location
     * @param bitSet the BitSet to cache
     */
    public void putBitSetToCache(String loc, BitSet bitSet) {
        bitSetCache.put(loc, bitSet);
    }

    /**
     * Retrieves a BitSet from the cache based on the chunk location.
     * @param chunkLoc the location of the chunk
     * @return the BitSet associated with the chunk location, or null if not found
     */
    public BitSet getBitSetCacheEntry(Location chunkLoc) {
        return bitSetCache.get(formatChunkLocation(chunkLoc));
    }

    /**
     * Retrieves a BitSet from the cache based on the chunk location as a string.
     * @param chunkLoc formatted chunk location
     * @return the BitSet associated with the chunk location, or null if not found
     */
    public BitSet getBitSetCacheEntry(String chunkLoc) {
        return bitSetCache.get(chunkLoc);
    }

    /**
     * Returns a copy of the BitSet cache.
     * @return a copy of the BitSet cache
     */
    public HashMap<String, BitSet> getBitSetCacheCopy() {
        return new HashMap<>(bitSetCache);
    }

    /**
     * Removes a BitSet from the cache based on the chunk location.
     * @param chunkLoc the location of the chunk
     */
    public void removeBitSetFromCache(Location chunkLoc) {
        bitSetCache.remove(formatChunkLocation(chunkLoc));
    }

    /**
     * Formats a chunk location into a string representation.
     * The format is: worldName_blockX/blockY/blockZ
     * @param chunkLoc The location of the chunk.
     * @return A string representing the chunk location.
     */
    public static String formatChunkLocation(Location chunkLoc) {
        return chunkLoc.getWorld().getName() + "_" + chunkLoc.getBlockX() + "/" + chunkLoc.getBlockY() + "/" + chunkLoc.getBlockZ();
    }

    /**
     * Add block to cache
     * @param chunkLoc The string representation of the chunk location.
     */
    public void addBlockToCache(String chunkLoc, int bitSetIndex) {
        BitSet bitSet = getBitSetCacheEntry(chunkLoc);

        // Create a new bitset with the full chunk capacity
        if (bitSet == null) {
            bitSet = new BitSet(TOTAL_BLOCKS_PER_CHUNK + 1);
        }

        bitSet.set(bitSetIndex);
        putBitSetToCache(chunkLoc, bitSet);
    }

    /**
     * Remove block from cache
     * @param chunkLoc The string representation of the chunk location.
     * @param bitSetIndex The index of the block in the BitSet.
     */
    public void removeBlockFromCache(Location chunkLoc, int bitSetIndex) {
        BitSet bitSet = getBitSetCacheEntry(formatChunkLocation(chunkLoc));

        if (bitSet != null) {
            bitSet.clear(bitSetIndex);
            putBitSetToCache(formatChunkLocation(chunkLoc), bitSet);
        }
    }

    /**
     * Converts a Bukkit Location to a chunk-relative index.
     * @param loc The location to convert.
     * @return The chunk-relative index (0-4095).
     */
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