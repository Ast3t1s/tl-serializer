import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoreTest {

    @Test
    public void serializationTest() throws IOException {
        List<Integer> list = new ArrayList<Integer>();
        list.add(5);
        list.add(6);
        List<String> list1 = new ArrayList<String>();
        list1.add("one");
        list1.add("two");
        InternalObject internalObject = new InternalObject(1, "string");
        TestObject testObject = new TestObject(1, "Test", System.currentTimeMillis(), list, list1, true, internalObject);
        byte[] data = testObject.toBytes();
        TestObject testObject1 = TestObject.fromBytes(data);
        Assert.assertEquals(testObject.getId(), testObject1.getId());
        Assert.assertEquals(testObject.getName(), testObject1.getName());
        Assert.assertEquals(testObject.getTime(), testObject1.getTime());
        Assert.assertEquals(testObject.getList(), testObject1.getList());
        Assert.assertEquals(testObject.getList1(), testObject1.getList1());
        Assert.assertEquals(testObject.isValue(), testObject1.isValue());
        Assert.assertEquals(testObject.getInternalObject().getId(), testObject1.getInternalObject().getId());
        Assert.assertEquals(testObject.getInternalObject().getStr(), testObject1.getInternalObject().getStr());
    }
}
