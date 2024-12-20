package jdbc;

import java.sql.*;
import java.util.*;

public class ChatRoomService {
	
	String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
	String ID = "root";
	String PW = "abcd1234";
	
	public List<ChatRoom> getChatRooms(String userId) {
	    // 수정된 SQL 쿼리: chat_rooms 테이블에서 owner_id 또는 user_id에 해당하는지 확인
	    String sql = "SELECT room_id, room_name, owner_id " +
	                 "FROM chat_rooms " +
	                 "WHERE owner_id = ? OR EXISTS (SELECT 1 FROM chat_rooms WHERE room_id = chat_rooms.room_id AND user_id = ?)";
	    
	    List<ChatRoom> chatRooms = new ArrayList<>();
	    try (Connection conn = DriverManager.getConnection(url, ID, PW);
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId); // owner_id 확인
	        pstmt.setString(2, userId); // user_id 확인
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
	
	public boolean addFriendToChatRoom(int roomId, String friendId) throws SQLException {
	    String query = "INSERT INTO chat_room_participants (room_id, user_id) VALUES (?, ?)";
	    try (Connection conn = DriverManager.getConnection(url, ID, PW);
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, roomId);
	        pstmt.setString(2, friendId);
	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw e; // 예외 전달
	    }
	}




}
