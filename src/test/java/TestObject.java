import com.tl.serialization.TLContext;
import com.tl.serialization.TLObject;
import com.tl.serialization.core.TLDeserializer;
import com.tl.serialization.core.TLSerializer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class TestObject extends TLObject {

    public static TestObject fromBytes(byte[] data) throws IOException {
        return TLContext.INSTANCE.deserialize(new TestObject(), data);
    }

    private int id;
    private String name;
    private long time;
    private boolean value;
    private List<Integer> list;
    private List<String> list1;
    private InternalObject internalObject;

    public TestObject() {

    }

    public TestObject(int id, String name, long time, List<Integer> list, List<String> list1, boolean value, InternalObject internalObject) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.list = list;
        this.list1 = list1;
        this.value = value;
        this.internalObject = internalObject;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public List<String> getList1() {
        return list1;
    }

    public void setList1(List<String> list1) {
        this.list1 = list1;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public InternalObject getInternalObject() {
        return internalObject;
    }

    public void setInternalObject(InternalObject internalObject) {
        this.internalObject = internalObject;
    }

    @Override
    public void deserialize(@NotNull TLDeserializer deserializer) throws IOException {
        id = deserializer.getInt(1);
        name = deserializer.getString(2);
        time = deserializer.getLong(3);
        list = deserializer.getListOfInts(4);
        list1 = deserializer.getListOfString(5);
        value = deserializer.getBool(6);
        byte[] wrap = deserializer.nullSafeBytes(7);
        if (wrap != null) {
            internalObject = new InternalObject(wrap);
        }
    }

    @Override
    public void serialize(@NotNull TLSerializer serializer) throws IOException {
        serializer.serializeInt(1, id);
        serializer.serializeString(2, name);
        serializer.serializeLong(3, time);
        serializer.serializeListOfInts(4, list);
        serializer.serializeListOfStrings(5, list1);
        serializer.serializeBool(6, value);
        serializer.serializeObject(7, internalObject);
    }
}
