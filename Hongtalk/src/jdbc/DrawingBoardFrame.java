package jdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class DrawingBoardFrame extends JFrame {
    private final Socket socket;
    private final int roomId;
    private PrintWriter out;
    private BufferedReader in;
    private JPanel canvas;
    private Graphics2D graphics;
    private int lastX, lastY;

    private Color currentColor = Color.BLACK; // 현재 색상
    private int currentThickness = 3; // 현재 선 두께

    public DrawingBoardFrame(Socket socket, int roomId) {
        this.socket = socket;
        this.roomId = roomId;

        try {
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "서버 연결에 문제가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        setTitle("Drawing Board - Room " + roomId);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();

        // 서버로부터 데이터 수신
        new Thread(this::receiveDrawingData).start();

        setVisible(true);
    }

    private void initUI() {
        // 캔버스 초기화
        canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (graphics == null) {
                    graphics = (Graphics2D) g.create();
                }
            }
        };
        canvas.setBackground(Color.WHITE);
        canvas.setPreferredSize(new Dimension(400, 300));

        // 마우스 이벤트 추가
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
            }
        });

        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // 현재 클라이언트에서 그림 그리기
                drawLine(lastX, lastY, x, y, currentColor, currentThickness);

                // 서버로 데이터 전송
                sendDrawData(lastX, lastY, x, y, currentColor, currentThickness);

                lastX = x;
                lastY = y;
            }
        });

        add(new JScrollPane(canvas), BorderLayout.CENTER);

        // 도구 패널 추가
        JPanel toolPanel = new JPanel();

        // 색상 선택 버튼
        JButton colorButton = new JButton("색상 선택");
        colorButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "색상 선택", currentColor);
            if (selectedColor != null) {
                currentColor = selectedColor;
            }
        });
        toolPanel.add(colorButton);

        // 두께 선택 스피너
        SpinnerModel thicknessModel = new SpinnerNumberModel(currentThickness, 1, 10, 1);
        JSpinner thicknessSpinner = new JSpinner(thicknessModel);
        thicknessSpinner.addChangeListener(e -> currentThickness = (int) thicknessSpinner.getValue());
        toolPanel.add(new JLabel("두께: "));
        toolPanel.add(thicknessSpinner);

        add(toolPanel, BorderLayout.NORTH);
    }

    private void drawLine(int x1, int y1, int x2, int y2, Color color, int thickness) {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(thickness)); // 선의 두께 설정
        g.drawLine(x1, y1, x2, y2);
    }

    private void sendDrawData(int startX, int startY, int endX, int endY, Color color, int thickness) {
        String data = String.format(
            "DRAW:%d,%d,%d,%d,%s,%d",
            startX, startY, endX, endY,
            String.format("#%06X", (color.getRGB() & 0xFFFFFF)), // RGB 색상 값
            thickness
        );

        if (out != null) {
            out.println(data);
        }
    }

    private void receiveDrawingData() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("DRAW:")) {
                    // DRAW 명령어 데이터 파싱
                    String[] parts = message.substring(5).split(",");
                    int startX = Integer.parseInt(parts[0]);
                    int startY = Integer.parseInt(parts[1]);
                    int endX = Integer.parseInt(parts[2]);
                    int endY = Integer.parseInt(parts[3]);
                    Color color = Color.decode(parts[4]); // 색상 파싱
                    int thickness = Integer.parseInt(parts[5]);

                    // 캔버스에 그리기
                    SwingUtilities.invokeLater(() -> drawLine(startX, startY, endX, endY, color, thickness));
                } else {
                    // 채팅 메시지 처리
                    System.out.println("Chat Message: " + message); // 또는 채팅 UI로 메시지 전달
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            dispose();
        }
    }
}
