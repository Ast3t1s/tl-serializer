import com.tl.serialization.TLObject;
import com.tl.serialization.core.TLSerializer;
import com.tl.serialization.core.TLDeserializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class InternalObject extends TLObject {

    private int id;
    private String str;

    public InternalObject() {

    }

    public InternalObject(byte[] data) {
        super();
        try {
            load(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InternalObject(int id, String str) {
        this.id = id;
        this.str = str;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public void deserialize(@NotNull TLDeserializer deserializer) throws IOException {
        id = deserializer.getInt(1);
        str = deserializer.getString(2);
    }

    @Override
    public void serialize(@NotNull TLSerializer serializer) throws IOException {
        serializer.serializeInt(1, id);
        serializer.serializeString(2, str);
    }
}
