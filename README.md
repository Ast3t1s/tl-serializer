# tl-serializer
Simple binary serialization like Telegram.
```
public class User extends TLObject {

    public static User fromBytes(byte[] data) throws IOException {
        return TLContext.INSTANCE.deserialize(new User(), data);
    }

    private int id;
    private String name;
    
    public User() {

    }
    
    public User(int id, String name) {
       this.id = id;
       this.name = name;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void deserialize(@NotNull TLDeserializer deserializer) throws IOException {
        id = deserializer.getInt(1);
        name = deserializer.getString(2);
    }

    @Override
    public void serialize(@NotNull TLSerializer serializer) throws IOException {
        serializer.serializeInt(1, id);
        serializer.serializeString(2, name);
    }
}

public static void main(String... args) {
    User user = new User(1, "username");
    byte[] data = user.toBytes();
    User user1 = User.fromBytes(data);
}
```
