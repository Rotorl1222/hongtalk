package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatMessageService {

    private final String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
    private final String ID = "root";
    private final String PW = "abcd1234";

    // 메시지 저장
    public boolean saveMessage(int roomId, String userId, String message) throws SQLException {
    	if (!isUserIdValid(userId)) {
            throw new SQLException("User ID " + userId + " does not exist in users table.");
        }
        String insertQuery = "INSERT INTO chat_messages (room_id, user_id, message, timestamp) VALUES (?, ?, ?, NOW())";

        try (Connection conn = DriverManager.getConnection(url, ID, PW);
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setInt(1, roomId);
            pstmt.setString(2, userId);
            pstmt.setString(3, message);

            return pstmt.executeUpdate() > 0;
        }
    }

    // 메시지 불러오기
    public List<String> getMessages(int roomId) throws SQLException {
        String selectQuery = "SELECT user_id, message, timestamp FROM chat_messages WHERE room_id = ? ORDER BY timestamp ASC";
        List<String> messages = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, ID, PW);
             PreparedStatement pstmt = conn.prepareStatement(selectQuery)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String message = rs.getString("message");
                Timestamp timestamp = rs.getTimestamp("timestamp");

                messages.add("[" + timestamp + "] " + userId + ": " + message);
            }
        }

        return messages;
    }
    
    
    private boolean isUserIdValid(String userId) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, ID, PW);
             PreparedStatement pstmt = conn.prepareStatement(checkQuery)) {
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}
