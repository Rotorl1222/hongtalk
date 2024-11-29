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
        JLabel ageLabel = new JLabel("나이 : ");
        JLabel idLabel = new JLabel("ID : ");
        JLabel passLabel = new JLabel("PASSWORD : ");
        
        JTextField nameField = new JTextField(10);
        JTextField ageField = new JTextField(10);
        JTextField idField = new JTextField(10);
        JPasswordField passField = new JPasswordField(10);
        
        JButton BackBtn = new JButton("뒤로가기");
        JButton registerBtn = new JButton("회원가입");
        
        c.add(BackBtn);
        c.add(nameLabel);
        c.add(nameField);
        c.add(ageLabel);
        c.add(ageField);
        c.add(idLabel);
        c.add(idField);
        c.add(passLabel);
        c.add(passField);
        c.add(registerBtn);

        // ageField 입력 제한 (숫자만 허용, 세 자릿수 이하)
        ageField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char character = e.getKeyChar();
                String text = ageField.getText();

                if (!Character.isDigit(character) || text.length() >= 3) {
                    e.consume();
                }
            }
        });

        // register 버튼 클릭 이벤트
        registerBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String ageText = ageField.getText().trim();
                String userId = idField.getText().trim();
                String userPassword = new String(passField.getPassword()).trim();

                // 입력 유효성 검사
                if (!checktext(name, ageText, userId, userPassword)) {
                    return; // 유효성 검사 실패 시 함수 종료
                }

                int age = Integer.parseInt(ageText); // ageField는 숫자만 받기 때문에 Integer 변환 가능
                boolean isValidUser = checkCanres(name, age, userId, userPassword);

                if (isValidUser) {
                    JOptionPane.showMessageDialog(c, "가입 성공!", "성공", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(c, "사용자 정보가 이미 존재합니다.", "가입 실패", JOptionPane.WARNING_MESSAGE);
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
	
	public boolean checktext(String name, String ageText, String userId, String userPassword) {
	    // 빈 칸 확인
	    if (name.isEmpty() || ageText.isEmpty() || userId.isEmpty() || userPassword.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "모든 필드를 채워주세요!", "입력 오류", JOptionPane.WARNING_MESSAGE);
	        return false;
	    }

	    // 나이 유효성 검사 (숫자 형식 및 범위)
	    try {
	        int age = Integer.parseInt(ageText);
	        if (age <= 0 || age > 120) {
	            JOptionPane.showMessageDialog(this, "나이는 1~120 사이의 값을 입력해주세요!", "입력 오류", JOptionPane.WARNING_MESSAGE);
	            return false;
	        }
	    } catch (NumberFormatException e) {
	        JOptionPane.showMessageDialog(this, "나이는 숫자 형식이어야 합니다!", "입력 오류", JOptionPane.WARNING_MESSAGE);
	        return false;
	    }

	    // 한글 포함 여부 확인 (아이디와 비밀번호)
	    String koreanRegex = ".*[\\u3131-\\uD79D]+.*";
	    if (userId.matches(koreanRegex) || userPassword.matches(koreanRegex)) {
	        JOptionPane.showMessageDialog(this, "ID와 PASSWORD는 한글을 포함할 수 없습니다!", "입력 오류", JOptionPane.WARNING_MESSAGE);
	        return false;
	    }

	    return true;
	}

	public boolean checkCanres(String name, int age, String userId, String userPassword) {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;

	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver");
	        conn = DriverManager.getConnection(url, ID, PW);

	        // 사용자 존재 여부 확인
	        String sql = "SELECT name, age, id, password FROM student WHERE name = ? AND id = ? AND password = ?";
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, name);
	        pstmt.setString(2, userId);
	        pstmt.setString(3, userPassword);

	        rs = pstmt.executeQuery();
	        boolean register = rs.next();

	        // 데이터 삽입 로직
	        if (!register) {
	            String res = "INSERT INTO student (name, age, id, password) values(?, ?, ?, ?)";
	            pstmt = conn.prepareStatement(res);
	            pstmt.setString(1, name);
	            pstmt.setInt(2, age);
	            pstmt.setString(3, userId);
	            pstmt.setString(4, userPassword);

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
