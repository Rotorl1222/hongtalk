package jdbc;

public class ChatRoom {
    private int roomId;           // 채팅방 ID
    private String roomName;      // 채팅방 이름
    private String createdBy;     // 생성자 ID

    public ChatRoom(int roomId, String roomName, String createdBy) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.createdBy = createdBy;
    }

    // Getter
    public int getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public String getCreatedBy() { return createdBy; }

    @Override
    public String toString() {
        return "ChatRoom [roomId=" + roomId + ", roomName=" + roomName + ", createdBy=" + createdBy + "]";
    }
}
