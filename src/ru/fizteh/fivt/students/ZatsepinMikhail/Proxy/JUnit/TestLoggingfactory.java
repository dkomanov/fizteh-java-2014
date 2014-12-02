package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.JUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMapFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.ProxyPackage.RealLogProxyFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TestLoggingfactory {
    static LoggingProxyFactory testedLogProxyFactory;
    static Table testedLoggedTable;
    static StringWriter writer;
    static List<Class<?>> typeList;
    static TableProvider provider;
    static String tableName;
    private static String providerDirectory;
    private static TableProviderFactory factory;
    private static Table testTable;

    @BeforeClass
    public static void setUpBeforeClass() {
        typeList = new ArrayList<>();
        typeList.add(Integer.class);
        typeList.add(String.class);
        typeList.add(Boolean.class);

        writer = new StringWriter();

        providerDirectory = Paths.get("").resolve("provider").toString();
        tableName = "testTable";
        factory = new MFileHashMapFactory();
        try {
            provider = factory.create(providerDirectory);
            testTable = provider.createTable(tableName, typeList);
        } catch (IOException e) {
            //suppress
        }

        testedLoggedTable
                    = (Table) new RealLogProxyFactory().wrap(writer, mock(FileMap.class), Table.class);

    }

    @Test
    public void testIntZeroArguments() {
        testedLoggedTable.rollback();
        System.out.println(writer);
        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.has("timestamp"));
        assertTrue(result.has("class"));
        assertTrue(result.has("method"));
        assertTrue(result.has("arguments"));
        assertTrue(result.has("returnValue"));
        assertTrue(!result.has("thrown"));

        assertTrue(result.get("method").equals("rollback"));
        assertTrue(result.get("class").
                equals("ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap$$EnhancerByMockitoWithCGLIB$$b7c951db"));
        JSONArray args = result.getJSONArray("arguments");
        assertTrue(args.length() == 0);

        writer.flush();
    }

    @Test
    public void testStringOneArgument() {
        testedLoggedTable.get("key");

        JSONObject result = new JSONObject(writer.toString());
        assertTrue(result.has("timestamp"));
        assertTrue(result.has("class"));
        assertTrue(result.has("method"));
        assertTrue(result.has("arguments"));
        assertTrue(result.has("returnValue"));
        assertTrue(!result.has("thrown"));

        assertTrue(result.get("method").equals("get"));
        JSONArray args = result.getJSONArray("arguments");
        assertTrue(args.length() == 1);
        assertTrue(args.get(0).equals("key"));

        writer.flush();
    }


}
