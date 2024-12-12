package jdbc;

import java.sql.*;
import java.util.*;
import javax.swing.*;

public class FriendService {
	
	String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
	String ID = "root";
	String PW = "abcd1234";

	
	public List<Friends> getFriends(String userId) {
	    String sql = "SELECT u.id, u.name, u.age FROM users u JOIN friends f ON u.id = f.friend_id WHERE f.user_id = ?";
	    List<Friends> friends = new ArrayList<>();
	    try (Connection conn = DriverManager.getConnection(url, ID, PW);
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, userId);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            String id = rs.getString("id");
	            String name = rs.getString("name");
	            int age = rs.getInt("age");
	            friends.add(new Friends(id, name, age));
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return friends;
	}
	
	public boolean addFriend(String userId, String friendId) throws SQLException {
	    Connection conn = null;
	    PreparedStatement pstmt = null;

	    try {
	        conn = DriverManager.getConnection(url, ID, PW);

	        // 친구 검색
	        String findFriendSQL = "SELECT id, name, age FROM users WHERE id = ?";
	        pstmt = conn.prepareStatement(findFriendSQL);
	        pstmt.setString(1, friendId);
	        ResultSet rs = pstmt.executeQuery();

	        if (!rs.next()) {
	            throw new SQLException("친구 ID를 찾을 수 없습니다.");
	        }

	        // 친구 추가
	        String addFriendSQL = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
	        pstmt = conn.prepareStatement(addFriendSQL);
	        pstmt.setString(1, userId);
	        pstmt.setString(2, friendId);
	        pstmt.executeUpdate();
	    } finally {
	        if (pstmt != null) pstmt.close();
	        if (conn != null) conn.close();
	    }
		return true;
	}
	
	
	private void updateFriendList(JTextArea friendListArea, List<String> friendList) {
	    // 기존 친구 목록을 초기화하고 새 목록으로 갱신
	    StringBuilder updatedText = new StringBuilder("참여한 친구:\n");
	    for (String friend : friendList) {
	        updatedText.append(friend).append("\n");
	    }
	    friendListArea.setText(updatedText.toString());
	}


	public boolean inviteFriendsToRoom(int roomId, List<String> friendIds, String roomName) {
        String sql = "INSERT INTO chat_rooms (room_id, user_id, room_name) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE room_name = VALUES(room_name)";
        try (Connection conn = DriverManager.getConnection(url, ID, PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (String friendId : friendIds) {
                pstmt.setInt(1, roomId);
                pstmt.setString(2, friendId);
                pstmt.setString(3, roomName);
                pstmt.addBatch(); // Batch 처리
            }
            System.out.println("친구 초대 성공: roomId = " + roomId + ", friendId = " + friendIds);
            pstmt.executeBatch(); // 실행
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

	public void removeFriend(String friendId) throws SQLException {
        String sql = "DELETE FROM friends WHERE friend_id = ?";
        try (Connection conn = DriverManager.getConnection(url, ID, PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, friendId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("친구 삭제 중 오류 발생.");
        }
    }


}
