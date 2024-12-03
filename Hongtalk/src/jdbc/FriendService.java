package jdbc;

import java.sql.*;
import java.util.*;

public class FriendService {
	
	String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false";
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



}
