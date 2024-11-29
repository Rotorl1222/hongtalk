package jdbc;

import java.io.*;
import java.net.*;
import java.util.*;

public class MultiClient {
    public static void main(String[] args) {
        MultiClient multiClient = new MultiClient();
        multiClient.start();
    }

    public void start() {
        Socket socket = null;
        BufferedReader in = null;
        try {
            socket = new Socket("127.0.0.1", 8020);
            socket.setSoTimeout(30000);
            System.out.println("[서버와 연결되었습니다]");

            String name = "user" + (int) (Math.random() * 10);
            Thread sendThread = new SendThread(socket, name);
            sendThread.start();

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (in != null) {
                String inputMsg = in.readLine();
                if (inputMsg == null || ("[" + name + "]님이 나가셨습니다").equals(inputMsg)) {
                    break;
                }
                System.out.println("From:" + inputMsg);
            }
        } catch (IOException e) {
            System.out.println("[IOException 발생]: " + e.getMessage());

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[서버 연결종료]");
    }
}

class SendThread extends Thread {
    private final Socket socket;
    private final String name;
    private final Scanner scanner = new Scanner(System.in);

    public SendThread(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    @Override
    public void run() {
        try (PrintStream out = new PrintStream(socket.getOutputStream())) {
            // 최초 1회는 클라이언트의 이름을 서버에 전송
            out.println(name);
            out.flush();

            while (true) {
                String outputMsg = scanner.nextLine();
                out.println(outputMsg);
                out.flush();
                if ("quit".equals(outputMsg)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
