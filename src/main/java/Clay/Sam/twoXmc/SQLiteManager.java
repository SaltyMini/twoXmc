package Clay.Sam.twoXmc;

import java.sql.*;
import java.util.BitSet;
import java.util.HashMap;

public class SQLiteManager {

    private String dbURL;
    private Connection connection;



    public SQLiteManager(String pluginDataPath) {
        this.dbURL = TwoXmc.getDbURL();
    }


    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbURL);
            System.out.println("Connected to SQLite database.");
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

    /**
     * Retrieves a BitSet by ID, returns null if not found
     */
    public BitSet getBitSet(String id) throws SQLException {
        String sql = "SELECT bitset_data FROM bitsets WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] data = rs.getBytes("bitset_data");
                return BitSet.valueOf(data);
            }
        }
        return null; // Not found
    }

    /**
     * Checks if a BitSet exists for the given ID
     */
    public boolean hasBitSet(String id) throws SQLException {
        String sql = "SELECT 1 FROM bitsets WHERE id = ? LIMIT 1";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
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

    public void quickSaveBitSets(HashMap<String, BitSet> bitSets) {
        for(String id : bitSets.keySet()) {
            BitSet bitSet = bitSets.get(id);
            try {
                setBitSet(id, bitSet);
            } catch (SQLException e) {
                System.err.println("Error saving BitSet with ID " + id + ": " + e.getMessage());
            }
        }
    }
}
