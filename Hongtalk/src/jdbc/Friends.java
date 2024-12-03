package jdbc;

public class Friends {
    private String id;      // 친구 아이디
    private String name;    // 친구 이름
    private int age;        // 친구 나이

    public Friends(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    // Getter
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return "Friends [id=" + id + ", name=" + name + ", age=" + age + "]";
    }
}

