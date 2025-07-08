package Clay.Sam.twoXmc;

import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.BitSet;
import java.util.HashMap;

public class SQLiteManager {

    private Connection connection;

    private static SQLiteManager instance;
    private final Plugin plugin;


    public SQLiteManager() {
        plugin = TwoXmc.getPlugin();
    }

    public static SQLiteManager getInstance() {
        if(instance == null) {
            instance = new SQLiteManager();
        }
        return instance;
    }

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(TwoXmc.getDbURL());
            plugin.getLogger().info("Connected to SQLite database.");
            // Initialize the table after connecting
            createTable();
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            plugin.getLogger().info("Disconnected from SQLite database");
        }
    }

    public void createTable() throws SQLException {
        String sql = """
        CREATE TABLE IF NOT EXISTS bitsets (
            id TEXT PRIMARY KEY,
            bitset_data BLOB NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            plugin.getLogger().info("Table created successfully");
        }
    }

    public BitSet getBitSet(String id) throws SQLException {
        String sql = "SELECT bitset_data FROM bitsets WHERE id = ?";
        plugin.getLogger().info("Getting BitSet with ID: " + id);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id); // Set parameter BEFORE executing
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    byte[] data = rs.getBytes("bitset_data");
                    return BitSet.valueOf(data);
                }
            }
        }
        return null; // Not found
    }

    public boolean hasBitSet(String id) throws SQLException {
        String sql = "SELECT 1 FROM bitsets WHERE id = ? LIMIT 1";
        plugin.getLogger().info("Checking if BitSet with ID exists: " + id);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, id); // Fixed: Set parameter BEFORE executing
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next(); // Returns true if a row exists
            }
        }
    }


    /**
     * Sets a BitSet, replacing any existing one with the same ID
     */
    public void setBitSet(String id, BitSet bitSet) throws SQLException {
        String sql = "INSERT OR REPLACE INTO bitsets (id, bitset_data) VALUES (?, ?)";
        plugin.getLogger().info("Saving BitSet with ID: " + id);

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setBytes(2, bitSet.toByteArray());
            pstmt.executeUpdate();
        }
    }

    public void quickSaveBitSets(HashMap<String, BitSet> bitSets) throws SQLException {
        String sql = "INSERT OR REPLACE INTO bitsets (id, bitset_data) VALUES (?, ?)";
        
        try {
            connection.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (String id : bitSets.keySet()) {
                    try {
                        BitSet bitSet = bitSets.get(id);
                        pstmt.setString(1, id);
                        pstmt.setBytes(2, bitSet.toByteArray());
                        pstmt.addBatch();
                    } catch (NullPointerException e) {
                        plugin.getLogger().warning("Skipping null BitSet for ID: " + id);
                    } catch (Exception e) {
                        plugin.getLogger().severe("Error processing BitSet for ID: " + id + " - " + e.getMessage());
                        throw e; // Re-throw to handle in outer catch
                    }
                }
                pstmt.executeBatch();
            }
            
            connection.commit(); // Commit transaction
            plugin.getLogger().info("Successfully saved " + bitSets.size() + " BitSets");
            
        } catch (SQLException e) {
            connection.rollback(); // Rollback on error
            plugin.getLogger().severe("Error saving BitSets: " + e.getMessage());
            throw e; // Re-throw to let caller handle
        } finally {
            connection.setAutoCommit(true); // Restore auto-commit
        }
    }
}