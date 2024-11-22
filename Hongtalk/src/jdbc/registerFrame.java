package jdbc;

import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class registerFrame extends JFrame {
	
	String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false";
	String ID = "root";
	String PW = "abcd1234";
	
	public registerFrame () {
		
		setTitle("회원가입 페이지");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        
        
        JLabel nameLabel = new JLabel("이름 : ");
        JLabel idLabel = new JLabel("ID : ");
        JLabel passLabel = new JLabel("PASSWORD : ");
        
        JTextField nameField = new JTextField(10);
        JTextField idField = new JTextField(10);
        JPasswordField passField = new JPasswordField(10);
        
        JButton BackBtn = new JButton("뒤로가기");
        JButton registerBtn = new JButton("회원가입");
        
        c.add(BackBtn);
        c.add(nameLabel);
        c.add(nameField);
        c.add(idLabel);
        c.add(idField);
        c.add(passLabel);
        c.add(passField);
        c.add(registerBtn);
        
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String name = nameField.getText();
                String userId = idField.getText();
                String userPassword = new String(passField.getPassword());
                boolean isValidUser = checkCanres(name, userId, userPassword);

                if (isValidUser) {
                    JOptionPane.showMessageDialog(c, "가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showConfirmDialog(c, "사용자 정보 존재", "가입 실패",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        BackBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		FrameConnection l = new FrameConnection();
        		l.setVisible(true);
        		dispose();
        	}
        });
        
        setSize(360, 600);

	}
	
	
	public boolean checkCanres(String name, String userId, String userPassword) {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        conn = DriverManager.getConnection(url, ID, PW);

	        // 사용자 존재 여부 확인
	        String sql = "SELECT name, id, password FROM student WHERE name = ? AND id = ? AND password = ?";
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, name);
	        pstmt.setString(2, userId);
	        pstmt.setString(3, userPassword);

	        rs = pstmt.executeQuery();
	        boolean register = rs.next();

	        // 데이터 삽입 로직
	        if(!register) {
	            String res = "INSERT INTO student (name, id, password) values(?, ?, ?)";
	            pstmt = conn.prepareStatement(res);
	            pstmt.setString(1, name);
	            pstmt.setString(2, userId);
	            pstmt.setString(3, userPassword);

	            int rowsAffected = pstmt.executeUpdate(); // 쿼리 실행
	            return rowsAffected > 0; // 삽입 성공 여부 반환
	        } else {
	            return false; // 사용자 정보가 이미 존재
	        }

	    } catch (ClassNotFoundException e) {
	        System.out.println("Driver 로딩 실패: " + e.getMessage());
	    } catch (SQLException e) {
	        System.out.println("서버 연결 실패: " + e.getMessage());
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    return false; // 예외 발생 시 false 반환
	}
	

}
