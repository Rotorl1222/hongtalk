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
    private final String userId;
    private final String roomName;
    private PrintWriter out;
    private Socket socket;
    JTextArea friendListArea = new JTextArea("참여한 친구:\n");

    public ChatFrame(int roomId, String userName, String userId, String roomName) throws IOException {
        this.roomId = roomId;
        this.userName = userName;
        this.userId = userId;
		this.roomName = roomName;

        setTitle("채팅방 - " + roomName);
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
        
     // 오른쪽 패널
        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(120, getHeight())); // 가로 120px
        rightPanel.setLayout(new BorderLayout());
        container.add(rightPanel, BorderLayout.EAST);

        // "친구 초대" 버튼
        JButton inviteButton = new JButton("친구 초대");
        inviteButton.addActionListener(e -> {
            // FriendInviteFrame을 띄움
            SwingUtilities.invokeLater(() -> new FriendInviteFrame(userId, roomId, roomName, this));
            
        });
        rightPanel.add(inviteButton, BorderLayout.NORTH);

        // 참여한 친구 목록
        
        friendListArea.setEditable(false);
        JScrollPane friendScrollPane = new JScrollPane(friendListArea);
        rightPanel.add(friendScrollPane, BorderLayout.CENTER);

        // 하단 부가기능 버튼 패널
        JPanel utilityPanel = new JPanel();
        utilityPanel.setLayout(new GridLayout(2, 1, 5, 5)); // 버튼 두 개 배치
        JButton utilityButton1 = new JButton("그림판");
        JButton utilityButton2 = new JButton("오늘의 운");

        utilityButton1.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new DrawingBoardFrame(socket, roomId); // ChatFrame의 socket을 전달
            });
        });
        utilityButton2.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new FortuneTellerFrame(this); // ChatFrame을 전달하여 운세 메시지를 전송
            });
        });


        utilityPanel.add(utilityButton1);
        utilityPanel.add(utilityButton2);

        rightPanel.add(utilityPanel, BorderLayout.SOUTH);

        
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
    
    public void addFriendToList(String friendName) {
        // 오른쪽 패널의 friendListArea에 친구 추가
        friendListArea.append(friendName + "\n");
    }


    private void connectToServer(JTextArea chatArea) {
    	
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
                    	if (msg.startsWith("DRAW:")) {
                            // 이 메시지는 그림 데이터로 처리되고, 채팅창에 출력하지 않음
                            continue;
                        }
                        System.out.println("Debug: Received raw message from server: " + msg); // 디버깅 로그 추가

                        // ':'로 메시지 구분
                        String[] parts = msg.split(":", 2); // "userName: message" 형식 파싱
                        if (parts.length == 2) {
                            String senderName = parts[0].trim();
                            String message = parts[1].trim();

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
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "서버에 연결할 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            System.out.println("Debug: Failed to connect to server: " + e.getMessage());
        }
    }
    
    



    private void sendMessage(JTextField inputField, JTextArea chatArea) {
    	
    	ChatMessageService chatMessageService = new ChatMessageService();
        String message = inputField.getText().trim();
        if (!message.isEmpty() && out != null) {
        	try {
                // 데이터베이스에 메시지 저장
                if (chatMessageService.saveMessage(roomId, userId, message)) {
                    out.println(userName + ": " + message);
                    inputField.setText("");
                } else {
                    chatArea.append("메시지를 저장하지 못했습니다.\n");
                }
            } catch (SQLException e) {
                chatArea.append("메시지 저장 중 오류가 발생했습니다.\n");
                e.printStackTrace();
            }
        }
    }
    
    public void sendFortuneToChat(String fortune) {
        if (out != null) {
            // 서버로 운세 메시지 전송
            out.println(userName + ": " + fortune);
        }
    }

}
