package jdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Scanner; 
import java.util.List;


public class ChatFrame extends JFrame {
    private final int roomId;
    private final String userName;
    private PrintWriter out;
    private Socket socket;

    public ChatFrame(int roomId, String userName) {
        this.roomId = roomId;
        this.userName = userName;

        setTitle("채팅방 - " + roomId);
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // UI 구성
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // 채팅 출력 영역
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        container.add(scrollPane, BorderLayout.CENTER);

        // 하단 입력 패널
        JPanel inputPanel = new JPanel(new BorderLayout());
        JTextField inputField = new JTextField();
        JButton sendButton = new JButton("Send");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        container.add(inputPanel, BorderLayout.SOUTH);
        
        ChatMessageService chatMessageService = new ChatMessageService();
        try {
        	
            List<String> messages = chatMessageService.getMessages(roomId);
            for (String message : messages) {
                chatArea.append(message + "\n");
            }
        } catch (SQLException e) {
            chatArea.append("이전 메시지를 불러오지 못했습니다.\n");
            e.printStackTrace();
        }

        setVisible(true);

        // 서버 연결
        connectToServer(chatArea);

        // 메시지 전송 로직
        sendButton.addActionListener(e -> sendMessage(inputField, chatArea));
        inputField.addActionListener(e -> sendMessage(inputField, chatArea));
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}// 연결 종료
                dispose();    // 창 닫기
            }
        });
    }
    
    

    private void connectToServer(JTextArea chatArea) {
    	
    	ChatMessageService chatMessageService = new ChatMessageService();
    	
        try {
            socket = new Socket("127.0.0.1", 8020);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 사용자 이름 및 채팅방 ID 전송
            out.println(roomId);

            // 서버에서 수신된 메시지 처리
            new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        System.out.println("Debug: Received raw message from server: " + msg); // 디버깅 로그 추가

                        // ':'로 메시지 구분
                        String[] parts = msg.split(":", 2); // "userName: message" 형식 파싱
                        if (parts.length == 2) {
                            String senderName = parts[0].trim();
                            String message = parts[1].trim();

                            chatMessageService.saveMessage(roomId, senderName, message);

                            // 디버깅 로그
                            System.out.println("Debug: Parsed senderName = " + senderName + ", message = " + message);

                            // 발신자와 수신자가 같은 경우 ':' 뒤의 메시지만 출력
                            if (senderName.equals(userName)) {
                                chatArea.append("나:" + message + "\n");
                            } else {
                                // 다른 사용자의 메시지는 전체 출력
                                chatArea.append(msg + "\n");
                            }
                        } else {
                            // ':' 없는 메시지는 그대로 출력
                            chatArea.append(msg + "\n");
                            System.out.println("Debug: Message does not contain ':', displaying as is.");
                        }
                    }
                } catch (IOException ex) {
                    chatArea.append("연결이 종료되었습니다.\n");
                    System.out.println("Debug: Exception occurred while reading messages: " + ex.getMessage());
                } catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "서버에 연결할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            System.out.println("Debug: Failed to connect to server: " + e.getMessage());
        }
    }



    private void sendMessage(JTextField inputField, JTextArea chatArea) {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && out != null) {
            out.println(userName + ": " + message);
            inputField.setText("");
        }
    }
}
