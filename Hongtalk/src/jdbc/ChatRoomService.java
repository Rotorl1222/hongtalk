package jdbc;

import java.sql.*;
import java.util.*;

public class ChatRoomService {
	
	String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false";
	String ID = "root";
	String PW = "abcd1234";
	
	public List<ChatRoom> getChatRooms(String userId) {
	    String sql = "SELECT room_id, room_name, owner_id FROM chat_rooms WHERE owner_id = ?";
	    List<ChatRoom> chatRooms = new ArrayList<>();
	    try (Connection conn = DriverManager.getConnection(url, ID, PW);
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            int roomId = rs.getInt("room_id");
	            String roomName = rs.getString("room_name");
	            String createdBy = rs.getString("owner_id");
	            chatRooms.add(new ChatRoom(roomId, roomName, createdBy));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return chatRooms;
	}
	
	public boolean addChatRoom(String roomName, String ownerId) throws SQLException {
	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    try {
	        conn = DriverManager.getConnection(url, ID, PW);

	        String addChatRoomSQL = "INSERT INTO chat_rooms (room_name, owner_id) VALUES (?, ?)";
	        pstmt = conn.prepareStatement(addChatRoomSQL);
	        pstmt.setString(1, roomName);
	        pstmt.setString(2, ownerId);
	        pstmt.executeUpdate();
	    } finally {
	        if (pstmt != null) pstmt.close();
	        if (conn != null) conn.close();
	    }
		return true;
	}



}
