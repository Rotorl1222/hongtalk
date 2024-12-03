package jdbc;

public class User {
    private String name;      // 이름
    private int age;          // 나이
    private String userId;    // 아이디
    private String password;  // 비밀번호

    // 기본 생성자
    public User() {
    	
    }

    // 모든 필드를 초기화하는 생성자
    public User(String name, int age, String userId, String password) {
        this.name = name;
        this.age = age;
        this.userId = userId;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User { " +
               "name='" + name + '\'' +
               ", age=" + age +
               ", userId='" + userId + '\'' +
               ", password='" + password + '\'' +
               " }";
    }
    
    
}
