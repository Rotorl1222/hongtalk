package jdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;


public class FortuneTellerFrame extends JFrame {
    private static final String[] MONEY_FORTUNE = {
        "돈이 많은 하루가 됩니다.",
        "금전적으로 여유가 생길 것입니다.",
        "돈이 들어오는 좋은 날입니다."
    };

    private static final String[] LOVE_FORTUNE = {
        "사랑은 찾아올 것입니다.",
        "연애운이 상승합니다.",
        "누군가가 당신을 좋아할 것입니다."
    };

    private static final String[] STUDY_FORTUNE = {
        "학업 성취도가 높아집니다.",
        "노력한 만큼 성과가 있을 것입니다.",
        "지식이 빠르게 쌓이는 하루입니다."
    };

    private ChatFrame chatFrame; // ChatFrame 객체를 참조

    public FortuneTellerFrame(ChatFrame chatFrame) {
        this.chatFrame = chatFrame;
        setTitle("운세 뽑기");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 창을 화면 중앙에 띄운다.

        // UI 구성
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        // 운세 결과 출력 라벨
        JLabel fortuneLabel = new JLabel("오늘의 운세를 뽑아보세요!", SwingConstants.CENTER);
        fortuneLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        container.add(fortuneLabel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton drawFortuneButton = new JButton("오늘의 운세 뽑기");
        buttonPanel.add(drawFortuneButton);
        container.add(buttonPanel, BorderLayout.SOUTH);

        // "오늘의 운세 뽑기" 버튼 클릭 시 랜덤으로 운세 출력
        drawFortuneButton.addActionListener(e -> {
            String fortune = getRandomFortune();
            fortuneLabel.setText(fortune);
            chatFrame.sendFortuneToChat(fortune); // 운세를 채팅방에 전송
        });

        setVisible(true);
    }

    // 랜덤으로 운세를 뽑는 메소드
    private String getRandomFortune() {
        Random random = new Random();
        int fortuneType = random.nextInt(3); // 0, 1, 2 랜덤 선택 (금전, 연애, 학업)

        switch (fortuneType) {
            case 0:
                return "[금전]: " + MONEY_FORTUNE[random.nextInt(MONEY_FORTUNE.length)];
            case 1:
                return "[연애]: " + LOVE_FORTUNE[random.nextInt(LOVE_FORTUNE.length)];
            case 2:
                return "[학업]: " + STUDY_FORTUNE[random.nextInt(STUDY_FORTUNE.length)];
            default:
                return "운세를 뽑을 수 없습니다.";
        }
    }

    // 메인 메소드 (테스트용)
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            // ChatFrame 객체를 생성하고, FortuneTellerFrame에 전달
//            new FortuneTellerFrame(new ChatFrame(1, "user", "user123", "채팅방"));
//        });
//    }
}
