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
                User user = checkUserCredentials(userId, userPassword);

                if (user != null) {
                	JOptionPane.showMessageDialog(c, "로그인 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                    new MainFrame(user); // 성공 시 mainFrame으로 이동
                    dispose();
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
    public User checkUserCredentials(String userId, String userPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, ID, PW);

            String sql = "SELECT * FROM users WHERE id = ? AND password = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.setString(2, userPassword);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String id = rs.getString("id");
                String password = rs.getString("password");
                
                System.out.println("name: " + name);
                System.out.println("age: " + age);
                System.out.println("id: " + id);
                System.out.println("password: " + password);

                return new User(name, age, id, password); // User 객체 생성
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
        return null; // 예외 발생 시 false 반환
    }

    public static void main(String[] args) {
        new FrameConnection();
    }
}
