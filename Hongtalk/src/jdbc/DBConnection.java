package jdbc;

import java.sql.*;

public class DBConnection {
	
	String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false";
	String ID = "root";
	String PW = "abcd1234";
	
	DBConnection() {
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			System.out.println("Driver 로딩 성공");
			Connection conn = DriverManager.getConnection(url, ID, PW);
			System.out.println("서버 연결 성공");
			
			stmt = conn.createStatement();
//			ResultSet srs = stmt.executeQuery()
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("Driver 로딩 실패: " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("서버 연결 실패: " + e.getMessage());
		}
	}
	
	private static void printData(ResultSet srs, String col1, String col2, String col3) 
			throws SQLException {
		while (srs.next()) {
			if (col1 != "")
				System.out.print(new String(srs.getString("name")));
				if (col2 != "")
				System.out.print("\t|\t" + srs.getString("id"));
				if (col3 != "")
				System.out.println("\t|\t" + new String(srs.getString("dept")));
				else
				System.out.println();
		}
		
	}
	
	public static void main(String[] args) {
		new DBConnection();
	}
}
