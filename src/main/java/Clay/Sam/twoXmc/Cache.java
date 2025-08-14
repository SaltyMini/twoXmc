package Clay.Sam.twoXmc;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cache {

    // Changed to final instance variable instead of static - prevents race conditions in initialization
    private final Map<String, BitSet> bitSetCache;
    private static Cache instance;
    private final SQLiteManager dbManager;
    private final Plugin plugin;

    private final int CACHE_SIZE = 1000;

    // Constants for the new world height
    public static final int MIN_WORLD_HEIGHT = -64;
    public static final int MAX_WORLD_HEIGHT = 320;
    public static final int WORLD_HEIGHT_RANGE = MAX_WORLD_HEIGHT - MIN_WORLD_HEIGHT;
    public static final int CHUNK_SIZE = 16;
    public static final int TOTAL_BLOCKS_PER_CHUNK = CHUNK_SIZE * CHUNK_SIZE * WORLD_HEIGHT_RANGE;

    public Cache() {
        // True LRU cache: accessOrder=true makes it order by access, removeEldestEntry handles automatic eviction
        bitSetCache = new LinkedHashMap<String, BitSet>(CACHE_SIZE + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, BitSet> eldest) {
                //save to database before removing
                try {
                    dbManager.setBitSet(eldest.getKey(), eldest.getValue());
                } catch (SQLException e) {
                    plugin.getLogger().severe("Failed to save BitSet to database for key: " + eldest.getKey());
                    plugin.getLogger().severe("Error: " + e.getMessage());
                }
                return size() > CACHE_SIZE; // Automatically removes LRU entries when cache exceeds size
            }
        };
        dbManager = SQLiteManager.getInstance();
        plugin = TwoXmc.getPlugin();
    }

    public static synchronized Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    public synchronized void updateBitSetInCache(Location location) {
        
        int index = locToChunkRelativeIndex(location);
        String chunkKey = formatChunkLocation(location);
        
        BitSet bitSet = bitSetCache.get(chunkKey);

        if(bitSet == null) {
            try {
                bitSet = dbManager.getBitSet(chunkKey);
                // Create new BitSet if not found in database - prevents null pointer exceptions
                if(bitSet == null) {
                    bitSet = new BitSet(TOTAL_BLOCKS_PER_CHUNK);
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to retrieve BitSet from database for location: " + location);
                plugin.getLogger().severe("Error: " + e.getMessage());
                return;
            }
        }

        bitSet.set(index);
        bitSetCache.put(chunkKey, bitSet); // This updates LRU order and triggers automatic eviction if needed
    }

    // Removed unloadOldBitSets() method - LinkedHashMap handles LRU eviction automatically

    public synchronized BitSet getBitSetCacheEntry(Location blockLocation) {
        String chunkKey = formatChunkLocation(blockLocation);
        BitSet bitSet = bitSetCache.get(chunkKey); // This marks entry as recently accessed
        
        if(bitSet == null) {
            try {
                bitSet = dbManager.getBitSet(chunkKey);
                // Create new BitSet if not found in database - prevents null pointer exceptions
                if(bitSet == null) {
                    bitSet = new BitSet(TOTAL_BLOCKS_PER_CHUNK);
                }
                bitSetCache.put(chunkKey, bitSet); // Add to cache and update LRU order
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to retrieve BitSet from database for chunk location: " + blockLocation);
                plugin.getLogger().severe("Error: " + e.getMessage());
                return new BitSet(TOTAL_BLOCKS_PER_CHUNK); // Return empty BitSet as fallback
            }
        }

        // Removed manual remove/put operations - LinkedHashMap with accessOrder=true handles LRU automatically
        return bitSet;
    }

    // Removed @Deprecated method - clean up unused code

    public synchronized HashMap<String, BitSet> getBitSetCacheCopy() {
        return new HashMap<>(bitSetCache);
    }

    // Rest of the methods remain unchanged as they don't affect thread safety or LRU behavior
    public static String formatChunkLocation(Location blockWithinChunk) {
        Location chunkLoc = blockWithinChunk.getChunk().getBlock(0, 0, 0).getLocation();
        return chunkLoc.getWorld().getName() + "_" + chunkLoc.getBlockX() + "/" + chunkLoc.getBlockY() + "/" + chunkLoc.getBlockZ();
    }

    public static int locToChunkRelativeIndex(Location loc) {
        int chunkX = loc.getBlockX() & 15;
        int chunkZ = loc.getBlockZ() & 15;
        int y = loc.getBlockY();

        if (y < MIN_WORLD_HEIGHT || y > MAX_WORLD_HEIGHT) {
            throw new IllegalArgumentException("Y coordinate " + y + " is outside valid range [" + MIN_WORLD_HEIGHT + ", " + MAX_WORLD_HEIGHT + "]");
        }

        int relativeY = y - MIN_WORLD_HEIGHT;
        return relativeY * 256 + chunkZ * 16 + chunkX;
    }
}