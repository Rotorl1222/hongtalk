package jdbc;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class FriendInviteFrame extends JFrame {

    private FriendService friendService = new FriendService();
    private String roomName;
    private String userId; // 로그인한 사용자의 ID
    private int roomId; // 초대할 방 ID
    private final ChatFrame chatFrame;
    private List<Friends> friendList;

    public FriendInviteFrame(String userId, int roomId, String roomName, ChatFrame chatFrame) {
        this.userId = userId;
        this.roomId = roomId;
        this.roomName = roomName;
        this.chatFrame = chatFrame;

        setTitle("친구 초대");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(200, 300);

        // 친구 목록 패널
        JPanel friendPanel = new JPanel();
        friendPanel.setLayout(new BoxLayout(friendPanel, BoxLayout.Y_AXIS));
        friendPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 친구 목록 가져오기
        friendList = friendService.getFriends(userId);
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (Friends friend : friendList) {
            listModel.addElement(friend.getName() + " (" + friend.getId() + ")");
        }

        JList<String> friendJList = new JList<>(listModel);
        friendJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // 다중 선택 허용
        JScrollPane scrollPane = new JScrollPane(friendJList);

        friendPanel.add(scrollPane);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("초대");
        JButton cancelButton = new JButton("취소");

        // 초대 버튼 동작
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> selectedFriends = friendJList.getSelectedValuesList();
                if (selectedFriends.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "초대할 친구를 선택해주세요.");
                    return;
                }

                // 선택된 친구 ID 리스트 생성
                List<String> selectedFriendIds = selectedFriends.stream()
                        .map(item -> item.substring(item.indexOf("(") + 1, item.indexOf(")")))
                        .toList();

                boolean success = friendService.inviteFriendsToRoom(roomId, selectedFriendIds, roomName);
                if (success) {
                    JOptionPane.showMessageDialog(null, "친구가 초대되었습니다.");
                    for (String friend : selectedFriends) {
                        String friendName = friend.substring(0, friend.indexOf(" ("));
                        chatFrame.addFriendToList(friendName);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "친구 초대 중 오류가 발생했습니다.");
                }
            }
        });

        // 취소 버튼 동작
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        // 프레임 구성
        add(friendPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // 화면 중앙에 표시
        setVisible(true);
    }
}
