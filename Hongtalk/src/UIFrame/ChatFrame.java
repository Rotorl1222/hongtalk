package UIFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatFrame extends JFrame {
    private FrameType frameType;
    private ActionHandler actionHandler;

    public ChatFrame(FrameType frameType) {
        this.frameType = frameType;
        initialize();
    }

    private void initialize() {
        setTitle(frameType == FrameType.MAIN ? "Main Chat Frame" : "Sub Chat Frame");
        setSize(360, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 현재 프레임만 닫음
        setLayout(new BorderLayout());

        // Initialize ActionHandler based on frameType
        if (frameType == FrameType.MAIN) {
            actionHandler = new JDBC();
        } else if (frameType == FrameType.SUB) {
            actionHandler = new Server();
        }

        // Add action button
        JButton actionButton = new JButton("Perform Action");
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (actionHandler != null) {
                    actionHandler.handleAction();
                }
            }
        });

        // Add exit button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 현재 프레임만 닫음
            }
        });

        // Add buttons to a panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(actionButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        switch (frameType) {
            case MAIN:
                drawMainUI(g);
                break;
            case SUB:
                drawSubUI(g);
                break;
        }
    }

    private void drawMainUI(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(50, 50, 300, 200);
        g.setColor(Color.WHITE);
        g.drawString("Main Chat Frame", 150, 150);
    }

    private void drawSubUI(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(50, 50, 300, 200);
        g.setColor(Color.BLACK);
        g.drawString("Sub Chat Frame", 150, 150);
    }
}
