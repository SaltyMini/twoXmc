package Clay.Sam.twoXmc;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Cache {

    private static LinkedHashMap<String, BitSet> bitSetCache;
    private static Cache instance;
    private final SQLiteManager dbManager;
    private final Plugin plugin;

    private final int CACHE_SIZE = 1000; // Maximum number of cached BitSets

    // Constants for the new world height
    public static final int MIN_WORLD_HEIGHT = -64;
    public static final int MAX_WORLD_HEIGHT = 320;
    public static final int WORLD_HEIGHT_RANGE = MAX_WORLD_HEIGHT - MIN_WORLD_HEIGHT; // 384 blocks
    public static final int CHUNK_SIZE = 16;
    public static final int TOTAL_BLOCKS_PER_CHUNK = CHUNK_SIZE * CHUNK_SIZE * WORLD_HEIGHT_RANGE; // 98,304 blocks



    public Cache() {
        bitSetCache = new LinkedHashMap<>();
        dbManager = SQLiteManager.getInstance();
        plugin = TwoXmc.getPlugin();
    }

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    public static HashMap<String, BitSet> getBitSetCache() {
        if (bitSetCache == null) {
            bitSetCache = new LinkedHashMap<>();
        }
        return bitSetCache;
    }

    public enum BitSetAction {
        ADD,
        REMOVE
    }

    public void updateBitSetInCache(Location location, BitSetAction action) {

        int index = locToChunkRelativeIndex(location);

        BitSet bitSet = bitSetCache.get(formatChunkLocation(location));

        //Does not contain, load from DB
        if(bitSet == null) {
            try {
                bitSet = dbManager.getBitSet(formatChunkLocation(location));
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to retrieve BitSet from database for location: " + location);
                plugin.getLogger().severe("Error: " + e.getMessage());
                return;
            }
        }

        bitSet.set(index, action == BitSetAction.ADD);

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
    @Deprecated
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
     * Formats a chunk location into a string representation.
     * The format is: worldName_blockX/blockY/blockZ
     * @param blockWithinChunk The location of the chunk.
     * @return A string representing the chunk location.
     */
    public static String formatChunkLocation(Location blockWithinChunk) {

        Location chunkLoc = blockWithinChunk.getChunk().getBlock(0, 0, 0).getLocation();

        return chunkLoc.getWorld().getName() + "_" + chunkLoc.getBlockX() + "/" + chunkLoc.getBlockY() + "/" + chunkLoc.getBlockZ();
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