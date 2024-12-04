package jdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;

public class ChatFrame extends JFrame {
    private final int roomId;
    private final String userName;
    private PrintWriter out;

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

        setVisible(true);

        // 서버 연결
        connectToServer(chatArea);

        // 메시지 전송 로직
        sendButton.addActionListener(e -> sendMessage(inputField, chatArea));
        inputField.addActionListener(e -> sendMessage(inputField, chatArea));
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                 // 연결 종료
                dispose();    // 창 닫기
            }
        });
    }
    
    

    private void connectToServer(JTextArea chatArea) {
        try {
            Socket socket = new Socket("127.0.0.1", 8020);
            socket.setSoTimeout(30000);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 사용자 이름 및 채팅방 ID 전송
            out.println(userName + "@" + roomId);
            

            // 서버에서 수신된 메시지 표시
            new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        chatArea.append(msg + "\n");
                    }
                } catch (IOException ex) {
                    chatArea.append("연결이 종료되었습니다.\n");
                }
            }).start();
        } catch (IOException e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(this, "서버에 연결할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
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
