package jdbc;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    public MainFrame(User user) {
        setTitle("메인 프레임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        // 상단 사용자 정보
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new FlowLayout());
        userInfoPanel.add(new JLabel("안녕하세요, " + user.getName() + "님!"));
        userInfoPanel.add(new JLabel("나이: " + user.getAge()));
        c.add(userInfoPanel, BorderLayout.NORTH);

        // 중앙 - 친구 목록
        JPanel friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
        friendPanel.setBorder(BorderFactory.createTitledBorder("친구 목록"));

        List<String> friends = getFriendList(user); // 친구 목록 불러오기 (임의 데이터)
        for (String friend : friends) {
            friendPanel.add(new JLabel(friend));
        }
        c.add(friendPanel, BorderLayout.WEST);

        // 중앙 - 채팅방 목록
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBorder(BorderFactory.createTitledBorder("채팅방 목록"));

        List<String> chatRooms = getChatRoomList(user); // 채팅방 목록 불러오기 (임의 데이터)
        for (String chatRoom : chatRooms) {
            chatPanel.add(new JLabel(chatRoom));
        }
        c.add(chatPanel, BorderLayout.EAST);

        // 하단 로그아웃 버튼
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(e -> {
            new FrameConnection();
            dispose();
        });
        c.add(logoutButton, BorderLayout.SOUTH);

        setSize(500, 400);
        setVisible(true);
    }

    // 임의의 친구 목록 데이터 반환
    private List<String> getFriendList(User user) {
        List<String> friends = new ArrayList<>();
        friends.add("친구1");
        friends.add("친구2");
        friends.add("친구3");
        return friends;
    }

    // 임의의 채팅방 목록 데이터 반환
    private List<String> getChatRoomList(User user) {
        List<String> chatRooms = new ArrayList<>();
        chatRooms.add("채팅방1");
        chatRooms.add("채팅방2");
        chatRooms.add("채팅방3");
        return chatRooms;
    }
}
