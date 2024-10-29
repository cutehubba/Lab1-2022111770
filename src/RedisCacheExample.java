import redis.clients.jedis.Jedis;
import com.google.gson.Gson;

record User(String name, int age) {
    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + '}';
    }
}
//B2
public class RedisCacheExample {
    private static Jedis jedis;
    private static Gson gson;

    public static void main(String[] args) {
        jedis = new Jedis("localhost", 6379);
        gson = new Gson();

        writeUser(new User("Alice", 30));
        writeUser(new User("Bob", 25));

        updateUser("Alice", 31);

        readUser("Alice");
        readUser("Bob");
        readUser("Charlie");

        deleteUser("Alice"); // 删除 Alice
        readUser("Alice"); // 确认删除是否成功
    }

    public static void writeUser(User user) {
        String userKey = "user:" + user.name().toLowerCase();
        jedis.set(userKey, gson.toJson(user));
        System.out.println("写入用户: " + user);
    }

    public static void updateUser(String name, int newAge) {
        String userKey = "user:" + name.toLowerCase();
        if (jedis.exists(userKey)) {
            User updatedUser = new User(name, newAge);
            jedis.set(userKey, gson.toJson(updatedUser));
            System.out.println("更新用户: " + updatedUser);
        } else {
            System.out.println("用户 " + name + " 不存在，无法更新。");
        }
    }

    public static void readUser(String name) {
        String userKey = "user:" + name.toLowerCase();
        String userJson = jedis.get(userKey);
        if (userJson != null) {
            User retrievedUser = gson.fromJson(userJson, User.class);
            System.out.println("读取用户: " + retrievedUser);
        } else {
            System.out.println("用户 " + name + " 在 Redis 中未找到。");
        }
    }

    public static void deleteUser(String name) {
        String userKey = "user:" + name.toLowerCase();
        if (jedis.exists(userKey)) {
            jedis.del(userKey);
            System.out.println("删除用户: " + name);
        } else {
            System.out.println("用户 " + name + " 不存在，无法删除。");
        }
    }
}
