package Clay.Sam.twoXmc;

import java.sql.*;
import java.util.BitSet;
import java.util.HashMap;

public class SQLiteManager {

    private String dbURL;
    private Connection connection;



    public SQLiteManager(String pluginDataPath) {
        this.dbURL = TwoXmc.getDbURL();
        // Remove pluginDataPath parameter if not needed, or use it if intended
    }


    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbURL);
            System.out.println("Connected to SQLite database.");
            // Initialize the table after connecting
            createTable();
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Disconnected from SQLite database");
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
            System.out.println("Table created successfully");
        }
    }

    public BitSet getBitSet(String id) throws SQLException {
        String sql = "SELECT bitset_data FROM bitsets WHERE id = ?";

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

        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            pstmt.setString(1, id);
            return rs.next(); // Returns true if a row exists
        }
    }

    /**
     * Sets a BitSet, replacing any existing one with the same ID
     */
    public void setBitSet(String id, BitSet bitSet) throws SQLException {
        String sql = "INSERT OR REPLACE INTO bitsets (id, bitset_data) VALUES (?, ?)";

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
                    BitSet bitSet = bitSets.get(id);
                    pstmt.setString(1, id);
                    pstmt.setBytes(2, bitSet.toByteArray());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            
            connection.commit(); // Commit transaction
            System.out.println("Successfully saved " + bitSets.size() + " BitSets");
            
        } catch (SQLException e) {
            connection.rollback(); // Rollback on error
            System.err.println("Error saving BitSets: " + e.getMessage());
            throw e; // Re-throw to let caller handle
        } finally {
            connection.setAutoCommit(true); // Restore auto-commit
        }
    }
}