package jdbc;

import javax.swing.*;

import UIFrame.Server;
import java.io.IOException;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
	
	jdbc.Server server;
	
    public MainFrame(User user) {
        setTitle("메인 프레임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new BorderLayout());
        
        String userID = user.getUserId();

        // 상단 사용자 정보
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new FlowLayout());
        userInfoPanel.add(new JLabel("안녕하세요, " + user.getName() + "님!"));
        userInfoPanel.add(new JLabel("나이: " + user.getAge()));
        c.add(userInfoPanel, BorderLayout.NORTH);

        // 중앙 - 친구 목록 패널
        JPanel friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
        friendPanel.setBorder(BorderFactory.createTitledBorder("친구 목록"));

        List<Friends> friends = getFriendList(user);
        System.out.println("Debug: 친구 목록 크기 = " + friends.size()); // 친구 목록의 크기 확인
        for (Friends friend : friends) {
            System.out.println("Debug: 친구 정보 = " + friend.getId() + ", " + friend.getName());
            JButton friendButton = new JButton(friend.getName() + " (" + friend.getId() + ")");
            friendButton.addActionListener(e -> showFriendInfo(friend));
            friendPanel.add(friendButton);
        }
        c.add(friendPanel);
        friendPanel.revalidate(); // 패널 갱신
        friendPanel.repaint(); 

        JButton addFriendButton = new JButton("친구 추가");
        addFriendButton.addActionListener(e -> {
            String friendId = JOptionPane.showInputDialog(this, "추가할 친구 ID를 입력하세요:");
            if (friendId != null && !friendId.trim().isEmpty()) {
                try {
                    FriendService friendService = new FriendService();
                    friendService.addFriend(user.getUserId(), friendId);
                    JOptionPane.showMessageDialog(this, "친구 추가 성공!");
//                    refreshFriendList(friendPanel, user);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "친구 추가 실패: " + ex.getMessage(), "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        c.add(addFriendButton, BorderLayout.WEST);

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBorder(BorderFactory.createTitledBorder("채팅방 목록"));

        List<ChatRoom> chatRooms = getChatRoomList(user); // 채팅방 목록 데이터베이스에서 가져오기
        for (ChatRoom chatRoom : chatRooms) {
            JButton chatButton = new JButton(chatRoom.getRoomName());
            chatButton.addActionListener(e -> openChatRoom(chatRoom, user));
            chatPanel.add(chatButton);
        }

        // 채팅방 생성 버튼
        JButton createChatButton = new JButton("채팅방 생성");
        createChatButton.addActionListener(e -> {
			try {
				createChatRoom(user);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        chatPanel.add(createChatButton);
        c.add(chatPanel, BorderLayout.EAST);

        // 하단 로그아웃 버튼
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(e -> {
            new FrameConnection();
            dispose();
        });
        c.add(logoutButton, BorderLayout.SOUTH);

        setSize(600, 400);
        setVisible(true);
    }
    
    private void startServer() {
        if (server == null) {
            try {
                server = new jdbc.Server();
                Thread serverThread = new Thread(() -> {
                    try {
                        server.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                serverThread.setDaemon(true);
                serverThread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // 데이터베이스에서 친구 목록 가져오기
    private List<Friends> getFriendList(User user) {
        FriendService friendService = new FriendService(); // 데이터베이스 처리 클래스
        return friendService.getFriends(user.getUserId());
    }

    // 데이터베이스에서 채팅방 목록 가져오기
    private List<ChatRoom> getChatRoomList(User user) {
        ChatRoomService chatRoomService = new ChatRoomService(); // 데이터베이스 처리 클래스
        return chatRoomService.getChatRooms(user.getUserId());
    }

    // 친구 정보 표시
    private void showFriendInfo(Friends friend) {
        JFrame friendInfoFrame = new JFrame(friend.getName() + " 정보");
        friendInfoFrame.setSize(300, 200);
        friendInfoFrame.setLayout(new BorderLayout());

        JTextArea infoArea = new JTextArea("ID: " + friend.getId() + "\n" +
                                           "이름: " + friend.getName() + "\n" +
                                           "나이: " + friend.getAge());
        infoArea.setEditable(false);
        friendInfoFrame.add(infoArea, BorderLayout.CENTER);

        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> friendInfoFrame.dispose());
        friendInfoFrame.add(closeButton, BorderLayout.SOUTH);

        friendInfoFrame.setVisible(true);
    }

    // 친구 추가 기능
    private void addFriend(User user) throws SQLException {
        String friendId = JOptionPane.showInputDialog(this, "추가할 친구의 ID를 입력하세요:", "친구 추가", JOptionPane.QUESTION_MESSAGE);
        String userId = user.getUserId();
        if (friendId != null && !friendId.isEmpty()) {
            FriendService friendService = new FriendService(); // 데이터베이스 처리 클래스
            boolean success = friendService.addFriend(userId, friendId);
            if (success) {
                JOptionPane.showMessageDialog(this, "친구가 성공적으로 추가되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "친구 추가에 실패했습니다. ID를 확인하세요.", "실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 채팅방 정보 표시
    private void showChatRoomInfo(ChatRoom chatRoom) {
        JFrame chatRoomInfoFrame = new JFrame(chatRoom.getRoomName() + " 정보");
        chatRoomInfoFrame.setSize(200, 300);
        chatRoomInfoFrame.setLayout(new BorderLayout());

        JTextArea infoArea = new JTextArea("방 ID: " + chatRoom.getRoomId() + "\n" +
                                           "방 이름: " + chatRoom.getRoomName() + "\n" +
                                           "생성자: " + chatRoom.getCreatedBy());
        infoArea.setEditable(false);
        chatRoomInfoFrame.add(infoArea, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> chatRoomInfoFrame.dispose());
        chatRoomInfoFrame.add(closeButton, BorderLayout.SOUTH);

        chatRoomInfoFrame.setVisible(true);
        
    }
    
 // 채팅방 정보 표시 및 열기
    private void openChatRoom(ChatRoom chatRoom, User user) {
        int roomId = chatRoom.getRoomId();
        startServer(); // 선택된 ChatRoom의 roomId로 서버 시작
        new ChatFrame(roomId, user.getName());
    }


    

    // 채팅방 생성 기능
    private void createChatRoom(User user) throws SQLException {
        String roomName = JOptionPane.showInputDialog(this, "생성할 채팅방 이름을 입력하세요:", "채팅방 생성", JOptionPane.QUESTION_MESSAGE);
        if (roomName != null && !roomName.isEmpty()) {
            ChatRoomService chatRoomService = new ChatRoomService(); // 데이터베이스 처리 클래스
            boolean success = chatRoomService.addChatRoom(roomName, user.getUserId());
            if (success) {
                JOptionPane.showMessageDialog(this, "채팅방이 성공적으로 생성되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "채팅방 생성에 실패했습니다.", "실패", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
