package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.JUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.ProxyPackage.RealLogProxyFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.AbstractStoreable;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestLoggingfactory {
    static Table testedLoggedTable;
    static TableProvider testedLoggedTableProvider;
    static StringWriter writer;
    static FileMap mockedFileMap;
    static MFileHashMap mockedMFileHashMap;

    @Before
    public void setUp() {
        writer = new StringWriter();
        mockedFileMap = mock(FileMap.class);
        when(mockedFileMap.get(null)).thenThrow(new IllegalArgumentException());
        testedLoggedTable
                = (Table) new RealLogProxyFactory().wrap(writer, mockedFileMap, Table.class);
        mockedMFileHashMap = mock(MFileHashMap.class);

        List<String> names = new ArrayList<>();
        names.add("table1");
        names.add("table2");
        when(mockedMFileHashMap.getTableNames()).thenReturn(names);
        testedLoggedTableProvider
                = (TableProvider) new RealLogProxyFactory().wrap(writer, mockedMFileHashMap, TableProvider.class);

    }

    @Test
    public void testIntZeroArguments() {
        testedLoggedTable.rollback();

        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.has("timestamp"));
        assertTrue(result.has("class"));
        assertTrue(result.has("method"));
        assertTrue(result.has("arguments"));
        assertTrue(result.has("returnValue"));
        assertTrue(!result.has("thrown"));

        assertTrue(result.getInt("returnValue") == 0);
        assertTrue(result.get("method").equals("rollback"));
        JSONArray args = result.getJSONArray("arguments");
        assertTrue(args.length() == 0);
    }

    @Test
    public void testStringMethodThrowExceptionOneArgument() {
        try {
            testedLoggedTable.get(null);
        } catch (IllegalArgumentException e) {
            //suppress
        }

        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.has("timestamp"));
        assertTrue(result.has("class"));
        assertTrue(result.has("method"));
        assertTrue(result.has("arguments"));
        assertTrue(!result.has("returnValue"));
        assertTrue(result.has("thrown"));

        assertTrue(result.get("method").equals("get"));
        assertTrue(result.get("thrown").equals("java.lang.IllegalArgumentException"));
        JSONArray args = result.getJSONArray("arguments");
        assertTrue(args.length() == 1);
        assertTrue(args.get(0).toString().equals("null"));
    }

    @Test
    public void testMethodTwoArguments() {
        Object[] fields = {"value"};
        AbstractStoreable value = new AbstractStoreable(fields, testedLoggedTable);
        testedLoggedTable.put("key", value);

        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.has("timestamp"));
        assertTrue(result.has("class"));
        assertTrue(result.has("method"));
        assertTrue(result.has("arguments"));
        assertTrue(result.has("returnValue"));
        assertTrue(!result.has("thrown"));

        assertTrue(result.get("method").equals("put"));
        JSONArray args = result.getJSONArray("arguments");
        assertTrue(args.length() == 2);
        assertTrue(args.get(0).equals("key"));
        assertTrue(args.get(1).equals(value.toString()));
    }

    @Test
    public void testMethodReturnList() {
        testedLoggedTableProvider.getTableNames();
        System.out.println(writer);

        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.has("timestamp"));
        assertTrue(result.has("class"));
        assertTrue(result.has("method"));
        assertTrue(result.has("arguments"));
        assertTrue(result.has("returnValue"));
        assertTrue(!result.has("thrown"));

        assertTrue(result.get("method").equals("getTableNames"));
        JSONArray args = result.getJSONArray("arguments");
        assertTrue(args.length() == 0);
        JSONArray returnValue = result.getJSONArray("returnValue");
        System.out.println(returnValue);
    }
}
