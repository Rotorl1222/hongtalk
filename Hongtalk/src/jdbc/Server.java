package jdbc;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
	
	 private int roomId; // 방 ID를 저장하는 필드

	    public Server(int roomId) {
	        this.roomId = roomId; // 생성자를 통해 roomId를 설정
	    }

	    public static void main(String[] args) {
	        // 이 부분에서 임의의 roomId로 시작할 수 있음
	        int roomId = 0; // 기본값
	        Server multiServer = new Server(roomId);
	        multiServer.start();
	    }

	    public void start() {
	        ServerSocket serverSocket = null;
	        Socket socket = null;

	        try {
	            serverSocket = new ServerSocket(8020);
	            serverSocket.setSoTimeout(40000);
	            while (true) {
	                System.out.println("[클라이언트 연결 대기 중]");
	                socket = serverSocket.accept();

	                // 클라이언트가 접속할 때마다 새로운 스레드 생성
	                ReceiveThread receiveThread = new ReceiveThread(socket, roomId); 
	                receiveThread.start();
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (serverSocket != null) {
	                try {
	                    serverSocket.close();
	                    System.out.println("[서버 종료]");
	                } catch (IOException e) {
	                    e.printStackTrace();
	                    System.out.println("[서버 소켓 통신 에러]");
	                }
	            }
	        }
	    }
}

class ReceiveThread extends Thread {
    static Map<Integer, List<PrintWriter>> chatRooms = new HashMap<>();
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int roomId;
    

    public ReceiveThread(Socket socket, int roomId) {
    	
        this.socket = socket;
        this.roomId = roomId;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            chatRooms.putIfAbsent(roomId, Collections.synchronizedList(new ArrayList<>()));
            chatRooms.get(roomId).add(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String name = "";
        try {
            // 최초 1회: 이름과 방 ID 수신
            String initialMessage = in.readLine(); // "userName@roomId"
            String[] parts = initialMessage.split("@");
            name = parts[0];
            roomId = Integer.parseInt(parts[1]); // roomId를 int로 변환

            System.out.println("[" + name + "] joined room " + roomId);

            sendAll(roomId, "[" + name + "]님이 들어오셨습니다.");

            // 메시지 수신 루프
            String message;
            while ((message = in.readLine()) != null) {
                if ("quit".equals(message)) break;
                sendAll(roomId, name + " >> " + message);
            }
        } catch (IOException e) {
            System.out.println("[" + name + "] 연결 끊김");
        } finally {
            sendAll(roomId, "[" + name + "]님이 나갔습니다.");
            List<PrintWriter> clients = chatRooms.get(roomId);
            if (clients != null) {
                clients.remove(out);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void sendAll(int roomId, String message) {
        List<PrintWriter> clients = chatRooms.get(roomId); // 해당 roomId의 클라이언트 리스트를 가져옴
        if (clients != null) { // 방이 존재할 경우에만 실행
            synchronized (clients) { // 해당 리스트를 동기화하여 안전하게 접근
                for (PrintWriter writer : clients) {
                    writer.println(message); // 메시지 전송
                    writer.flush(); // 버퍼 플러시
                }
            }
        }
    }


}

