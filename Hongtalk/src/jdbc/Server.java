package jdbc;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private ServerSocket serverSocket;
    private Map<Integer, List<ClientHandler>> chatRooms = new HashMap<>(); // 방 ID별 클라이언트 관리
    private boolean running = true;

    public void start() throws IOException {
        serverSocket = new ServerSocket(8020); // 고정 포트로 단일 서버 소켓 생성
        System.out.println("Server started on port 8020");

        while (running) {
        	try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // 클라이언트를 별도 스레드에서 처리
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            } catch (IOException e) {
                if (!running) {
                    System.out.println("Server is shutting down.");
                } else {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }
    
    public void addClientToRoom(int roomId, ClientHandler client) {
        synchronized (chatRooms) {
            chatRooms.putIfAbsent(roomId, new ArrayList<>());
            chatRooms.get(roomId).add(client);
        }
    }
    
    public void removeClientFromRoom(int roomId, ClientHandler client) {
        synchronized (chatRooms) {
            if (chatRooms.containsKey(roomId)) {
                chatRooms.get(roomId).remove(client);

                // 방에 클라이언트가 없으면 방 삭제
                if (chatRooms.get(roomId).isEmpty()) {
                    chatRooms.remove(roomId);
                }
            }
        }
    }



    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            // 클라이언트로부터 방 ID 수신
            int roomId = Integer.parseInt(in.readLine());
            System.out.println("Client joined Room ID: " + roomId);

            ClientHandler clientHandler = new ClientHandler(clientSocket, roomId, this);

            // 메시지 수신 및 처리
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message from Room " + roomId + ": " + message);
                if (message.startsWith("DRAW:")) {
                    // "DRAW:" 이후의 데이터를 그대로 브로드캐스트
                    String drawData = message.substring(5).trim();
                    System.out.println("Broadcasting DRAW data: " + drawData);
                    broadcastMessage(roomId, "DRAW:" + drawData);
                }  else {
                    broadcastMessage(roomId, message); // 일반 채팅 메시지 브로드캐스트
                }
            

            }

        } catch (IOException e) {
            System.err.println("Client connection error: " + e.getMessage());
        }
    }


    // 방에 있는 모든 클라이언트에게 메시지 전송
    public void broadcastMessage(int roomId, String message) {
    	System.out.println("brodcast on");
        List<ClientHandler> clients = chatRooms.get(roomId); // 해당 방의 클라이언트 리스트 가져오기
        if (clients != null) {
        	System.out.println("client != null");
            synchronized (clients) {
                for (ClientHandler client : clients) {
                    client.sendMessage(message); // 메시지 전송
                }
            }
        }
    }


    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        // 모든 클라이언트 소켓 닫기
        synchronized (chatRooms) {
            for (List<ClientHandler> handlers : chatRooms.values()) {
                for (ClientHandler handler : handlers) {
                    try {
                        handler.getClientSocket().close();
                    } catch (IOException e) {
                        System.err.println("Error closing client socket: " + e.getMessage());
                    }
                }
            }
        }

        chatRooms.clear(); // 모든 방 제거
        System.out.println("Server stopped.");
    }
    

}

class ClientHandler {
    private Socket clientSocket;
    private PrintWriter out;
    private int roomId;
    private Server server;

    public ClientHandler(Socket clientSocket, int roomId, Server server) throws IOException {
        this.clientSocket = clientSocket;
        this.roomId = roomId;
        this.server = server;
        this.out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

        // 방에 자신을 추가
        server.addClientToRoom(roomId, this);
    }

    public void flush() {
		// TODO Auto-generated method stub
		
	}

	public void println(String message) {
		// TODO Auto-generated method stub
		
	}

	public void sendMessage(String message) {
		System.out.println("message send");
        out.println(message);
        out.flush(); // 버퍼를 비워 즉시 전송
    }

    public void disconnect() {
        try {
            server.removeClientFromRoom(roomId, this); // 방에서 제거
            clientSocket.close(); // 소켓 닫기
        } catch (IOException e) {
            System.err.println("Error disconnecting client: " + e.getMessage());
        }
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
