package UIFrame;

import javax.swing.SwingUtilities;

public class ChatApplication {
	public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatFrame mainFrame = new ChatFrame(FrameType.MAIN);
            mainFrame.setVisible(true);

            ChatFrame subFrame = new ChatFrame(FrameType.SUB);
            subFrame.setVisible(true);
            subFrame.setLocation(450, 100); // 위치 조정
        });
    }

}
