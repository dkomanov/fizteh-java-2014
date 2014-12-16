package ru.fizteh.fivt.students.sautin1.telnet.tests;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.sautin1.telnet.proxy.LoggingProxyFactoryJSON;
import ru.fizteh.fivt.students.sautin1.telnet.shell.FileUtils;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.*;

import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 * Created by sautin1 on 12/15/14.
 */
public class LoggingProxyFactoryJSONTest {
    private LoggingProxyFactory proxyFactory;
    private Path testDir;
    private StoreableTableProviderFactory factory;
    private StoreableTableProvider provider;
    private StoreableTable storeableTable;
    private String tableName;
    private List<Class<?>> valueTypes;
    private Set<String> obligatoryTags;
    private String throwTag;
    private String returnTag;
    private StringWriter writer;

    @Before
    public void setUp() throws Exception {
        proxyFactory = new LoggingProxyFactoryJSON();
        testDir = Paths.get("").resolve("testDir");
        factory = new StoreableTableProviderFactory();
        provider = factory.create(testDir.toString());
        tableName = "testTable";
        valueTypes = new ArrayList<>();
        valueTypes.add(Integer.class);
        valueTypes.add(String.class);
        valueTypes.add(Byte.class);
        storeableTable = provider.createTable(tableName, valueTypes);
        obligatoryTags = new HashSet<>();
        obligatoryTags.add("timestamp");
        obligatoryTags.add("class");
        obligatoryTags.add("method");
        obligatoryTags.add("arguments");
        throwTag = "thrown";
        returnTag = "returnValue";
        writer = new StringWriter();
    }

    @After
    public void tearDown() throws Exception {
        try {
            provider.removeTable(tableName);
        } catch (Exception e) {
            // haven't created such table
        }
        FileUtils.clearDirectory(testDir);
    }

    public void checkTags(String logString, boolean checkReturn, boolean checkThrow) throws Exception {
        JSONObject result = new JSONObject(logString);
        for (String tag : obligatoryTags) {
            assertTrue(result.has(tag));
        }
        if (checkReturn) {
            assertTrue(result.has(returnTag));
        }
        if (checkThrow) {
            assertTrue(result.has(throwTag));
        }
    }

    @Test
    public void testWrapArrayList() throws Exception  {
        ArrayList arrayList = new ArrayList();
        StringWriter writer = new StringWriter();
        List list = (List) proxyFactory.wrap(writer, arrayList, List.class);

        try {
            list.indexOf(new Object[]{});
        } catch (Exception e) {
            // suppress
        }
        writer.flush();

        checkTags(writer.toString(), true, false);
    }

    @Test
    public void testWrapDatabase() throws Exception {
        Table table = (Table) proxyFactory.wrap(writer, storeableTable, Table.class);
        AutoCloseable autoCloseable = (AutoCloseable) proxyFactory.wrap(writer, storeableTable, AutoCloseable.class);

        try {
            table.toString();
            table.get("key");
            autoCloseable.close();
        } catch (Exception e) {
            // suppress
        }
        writer.flush();

        checkTags(writer.toString(), true, false);
    }

    @Test
    public void testWrapIterable() throws Exception {
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        StringWriter writer = new StringWriter();
        Map<String, Integer> map = (Map<String, Integer>) proxyFactory.wrap(writer, hashMap, Map.class);
        map.put("key1", 1);
        map.put("key2", 2);

        map.entrySet();
        writer.flush();

        checkTags(writer.toString(), true, false);
    }
}
