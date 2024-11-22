package jdbc;

import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class FrameConnection extends JFrame {
    // DB login data
    String url = "jdbc:mysql://localhost:3306/abcd?serverTimezone=UTC&useSSL=false";
    String ID = "root";
    String PW = "abcd1234";



    public FrameConnection() {
        setTitle("로그인 페이지");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new FlowLayout());

        JLabel idLabel = new JLabel("ID : ");
        JLabel passLabel = new JLabel("PASSWORD : ");
        JTextField idField = new JTextField(10);
        JPasswordField passField = new JPasswordField(10);
        JButton logBtn = new JButton("로그인");
        JButton RBtn = new JButton("회원가입");

        c.add(idLabel);
        c.add(idField);
        c.add(passLabel);
        c.add(passField);
        c.add(logBtn);
        c.add(RBtn);

        setSize(360, 600);
        setVisible(true);

        // 버튼 클릭 이벤트
        logBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = idField.getText();
                String userPassword = new String(passField.getPassword());
                boolean isValidUser = checkUserCredentials(userId, userPassword);

                if (isValidUser) {
                    JOptionPane.showMessageDialog(c, "로그인 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showConfirmDialog(c, "사용자 정보 없음", "로그인 실패",JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        RBtn.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		registerFrame r = new registerFrame();
        		r.setVisible(true);
        		dispose();
        	}
        });
        
    }

    // 데이터베이스에서 사용자 자격 증명을 확인하는 메서드
    public boolean checkUserCredentials(String userId, String userPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, ID, PW);

            String sql = "SELECT id, password FROM student WHERE id = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userPassword);

            rs = pstmt.executeQuery();
            return rs.next(); // 결과가 존재하면 true, 없으면 false

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

    public static void main(String[] args) {
        new FrameConnection();
    }
}
