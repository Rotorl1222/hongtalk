package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserService {
    
	String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
	String ID = "root";
	String PW = "abcd1234";


    // 유저 정보 수정
    public boolean updateUserInfo(String userId, String newName, int newAge) throws SQLException {
        String sql = "UPDATE users SET name = ?, age = ? WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(url, ID,PW);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setString(3, userId);

            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0; // 수정이 성공적으로 이루어졌다면 true 반환
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("유저 정보 수정 중 오류 발생.");
        }
    }
}
