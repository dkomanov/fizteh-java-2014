package ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Tests;

import org.junit.*;
import org.junit.rules.TemporaryFolder;
import ru.fizteh.fivt.proxy.LoggingProxyFactory;
import ru.fizteh.fivt.storage.structured.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.DatabaseTableProviderFactory;
import ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.Wrapper;

public class LoggingProxyFactoryTest {
    static JUnitTestInterface implementation;
    static List<Class<?>> columnTypes;
    static List<Class<?>> severalColumnTypes;
    Table table;
    Table severalColumnsTable;
    static TableProviderFactory factory;
    TableProvider provider;
    private static final String SINGLE_COLUMN_TABLE_NAME = "singleColumnTable";
    private static final String SEVERAL_COLUMNS_TABLE_NAME = "severalColumnsTable";
    static StringWriter stringWriter;
    static LoggingProxyFactory wrapper;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @BeforeClass
    public static void beforeClass() throws IOException {
        columnTypes = new ArrayList<Class<?>>() {
            {
                add(Integer.class);
            }
        };
        severalColumnTypes = new ArrayList<Class<?>>() {
            {
                add(Integer.class);
                add(String.class);
                add(Double.class);
            }
        };
        factory = new DatabaseTableProviderFactory();
        wrapper = new Wrapper();

    }

    @AfterClass
    public static void afterClass() {
        try {
            ((DatabaseTableProviderFactory) factory).close();
        } catch (Exception e) {
            //
        }
    }

    @Before
    public void beforeTest() throws IOException {
        stringWriter = new StringWriter();
        provider = factory.create(folder.getRoot().getPath());
        table = provider.createTable(SINGLE_COLUMN_TABLE_NAME, columnTypes);
        severalColumnsTable = provider.createTable(SEVERAL_COLUMNS_TABLE_NAME, severalColumnTypes);
    }

    @After
    public void afterTest() throws IOException {
        provider.removeTable(SINGLE_COLUMN_TABLE_NAME);
        provider.removeTable(SEVERAL_COLUMNS_TABLE_NAME);
    }

    public Storeable makeStoreable(int value) {
        try {
            return provider.deserialize(table, String.format("<row><col>%d</col></row>", value));
        } catch (ParseException e) {
            return null;
        }
    }

    @Test
    public void interfaceExecLogTest() throws JSONException {
        implementation = new JUnitTestInterface() {
            @Override
            public void execute() {
            }

            @Override
            public void supportingMethod() throws Exception {
            }

            @Override
            public int getAmount() {
                return 0;
            }

            @Override
            public String arrayGetter(List<String> names, int number) {
                return "";
            }
        };
        JUnitTestInterface proxy = (JUnitTestInterface)
                wrapper.wrap(stringWriter, implementation, JUnitTestInterface.class);

        long timestampBefore = System.currentTimeMillis();
        proxy.execute();
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"), implementation.getClass().getName());
        Assert.assertEquals(parser.getString("method"), "execute");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        stringWriter.flush();
    }

    @Test
    public void interfaceSupportFuncLogTest() throws Exception, JSONException {
        implementation = new JUnitTestInterface() {
            @Override
            public void execute() {
            }

            @Override
            public void supportingMethod() throws Exception {
                throw new Exception("Testing exceptions");
            }

            @Override
            public int getAmount() {
                return 0;
            }

            @Override
            public String arrayGetter(List<String> names, int number) {
                return "";
            }
        };
        JUnitTestInterface proxy = (JUnitTestInterface)
                wrapper.wrap(stringWriter, implementation, JUnitTestInterface.class);

        long timestampBefore = System.currentTimeMillis();
        try {
            proxy.supportingMethod();
        } catch (Exception e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("thrown"), "java.lang.Exception: Testing exceptions");
        Assert.assertEquals(parser.getString("class"), implementation.getClass().getName());
        Assert.assertEquals(parser.getString("method"), "supportingMethod");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        stringWriter.flush();
    }

    @Test
    public void getAmountLogTest() throws JSONException {
        implementation = new JUnitTestInterface() {
            @Override
            public void execute() {
            }

            @Override
            public void supportingMethod() throws Exception {
            }

            @Override
            public int getAmount() {
                table.put("key1", makeStoreable(1));
                table.put("key2", makeStoreable(1));
                table.put("key3", makeStoreable(1));
                return table.size();
            }

            @Override
            public String arrayGetter(List<String> names, int number) {
                return "";
            }

        };
        JUnitTestInterface proxy = (JUnitTestInterface)
                wrapper.wrap(stringWriter, implementation, JUnitTestInterface.class);

        long timestampBefore = System.currentTimeMillis();
        proxy.getAmount();
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"), implementation.getClass().getName());
        Assert.assertEquals(parser.getString("method"), "getAmount");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertEquals(parser.getInt("returnValue"), 3);
        table.remove("key1");
        table.remove("key2");
        table.remove("key3");
        stringWriter.flush();
    }

    @Test
    public void anotherGetAmountLogTest() throws JSONException {
        implementation = new JUnitTestInterface() {
            @Override
            public void execute() {
            }

            @Override
            public void supportingMethod() throws Exception {
            }

            @Override
            public int getAmount() {
                table.put("key1", makeStoreable(1));
                table.put("key2", makeStoreable(1));
                table.put("key3", makeStoreable(1));
                table.remove("key1");
                table.remove("key2");
                table.remove("key3");
                return table.rollback();
            }

            @Override
            public String arrayGetter(List<String> names, int number) {
                return "";
            }
        };
        JUnitTestInterface proxy = (JUnitTestInterface)
                wrapper.wrap(stringWriter, implementation, JUnitTestInterface.class);

        long timestampBefore = System.currentTimeMillis();
        proxy.getAmount();
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"), implementation.getClass().getName());
        Assert.assertEquals(parser.getString("method"), "getAmount");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertEquals(parser.getInt("returnValue"), 0);
        stringWriter.flush();
    }

    @Test
    public void arrayLogTest() throws JSONException {
        implementation = new JUnitTestInterface() {
            @Override
            public void execute() {
            }

            @Override
            public void supportingMethod() throws Exception {
            }

            @Override
            public int getAmount() {
                return 0;
            }

            @Override
            public String arrayGetter(List<String> names, int number) {
                return names.get(number).toString();
            }
        };
        List<String> testTableNames = new ArrayList<String>();
        testTableNames.add(SINGLE_COLUMN_TABLE_NAME);
        testTableNames.add(SEVERAL_COLUMNS_TABLE_NAME);
        JUnitTestInterface proxy = (JUnitTestInterface)
                wrapper.wrap(stringWriter, implementation, JUnitTestInterface.class);

        long timestampBefore = System.currentTimeMillis();
        proxy.arrayGetter(testTableNames, 0);
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"), implementation.getClass().getName());
        Assert.assertEquals(parser.getString("method"), "arrayGetter");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals("[\"singleColumnTable\",\"severalColumnsTable\"]", args.getJSONArray(0).toString());
        Assert.assertEquals(0, args.getInt(1));
        Assert.assertEquals(parser.getString("returnValue"), "singleColumnTable");
        stringWriter.flush();
    }

    @Test
    public void sizeLogTest() throws JSONException {
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.size();
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "size");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertEquals(parser.getInt("returnValue"), 0);
        stringWriter.flush();
    }

    @Test
    public void getLogTest() throws JSONException {
        table.put("key", makeStoreable(5));
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.get("key");
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "get");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals(args.getString(0), "key");
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals(parser.getString("returnValue"), "DatabaseTableRow[5]");
        table.remove("key");
        stringWriter.flush();
    }

    @Test
    public void putLogTest() throws JSONException {
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.put("key", makeStoreable(5));
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "put");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals(args.getString(0), "key");
        Assert.assertEquals(args.getString(1), "DatabaseTableRow[5]");
        Assert.assertTrue(parser.isNull("returnValue"));
        stringWriter.flush();
    }

    @Test
    public void removeLogTest() throws JSONException {
        table.put("key", makeStoreable(5));
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.remove("key");
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "remove");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals(args.getString(0), "key");
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals(parser.get("returnValue"), "DatabaseTableRow[5]");
        stringWriter.flush();
    }

    @Test
    public void commitLogTest() throws JSONException {
        table.put("key1", makeStoreable(1));
        table.put("key2", makeStoreable(2));
        table.put("key3", makeStoreable(3));
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        try {
            wrappedTable.commit();
        } catch (IOException e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "commit");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals(parser.getInt("returnValue"), 3);
        table.remove("key1");
        table.remove("key2");
        table.remove("key3");
        stringWriter.flush();
    }

    @Test
    public void rollbackLogTest() throws JSONException {
        table.put("key1", makeStoreable(1));
        table.put("key2", makeStoreable(2));
        table.put("key3", makeStoreable(3));
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.rollback();
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "rollback");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals(parser.getInt("returnValue"), 3);
        table.remove("key1");
        table.remove("key2");
        table.remove("key3");
        stringWriter.flush();
    }

    @Test
    public void getNameLogTest() throws JSONException {
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.getName();
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "getName");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertEquals(parser.getString("returnValue"), "singleColumnTable");
        stringWriter.flush();
    }

    @Test
    public void getColumnsCountLogTest() throws JSONException {
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.getColumnsCount();
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "getColumnsCount");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertEquals(parser.getInt("returnValue"), 1);
        stringWriter.flush();
    }

    @Test
    public void getColumnTypeLogTest() throws JSONException {
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.getColumnType(0);
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "getColumnType");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals(args.getInt(0), 0);
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals(parser.get("returnValue").toString(), Integer.class.toString());
        stringWriter.flush();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getColumnTypeExceptionLogTest() throws JSONException {
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedTable.getColumnType(1);
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "getColumnType");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals(args.getInt(0), 0);
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals(parser.get("returnValue").toString(), Integer.class.toString());
        stringWriter.flush();
    }

    @Test
    public void createTableLogTest() throws JSONException {
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        long timestampBefore = System.currentTimeMillis();
        try {
            wrappedProvider.createTable("new", list);
        } catch (IOException e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("createTable", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals("new", args.getString(0));
        Assert.assertEquals("[\"class java.lang.Integer\"]", args.getJSONArray(1).toString());
        Assert.assertTrue(!parser.isNull("returnValue"));
        String fold = folder.getRoot().getPath();
        String dir = new File(fold, "new").toString();
        Assert.assertEquals(String.format("ThreadSafeDatabaseTable[%s]", dir),
                parser.get("returnValue").toString());
        try {
            provider.removeTable("new");
        } catch (IOException e) {
            //
        }
        stringWriter.flush();
    }

    @Test
    public void getTableLogTest() throws JSONException {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        try {
            provider.createTable("new", list);
        } catch (IOException e) {
            //
        }
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedProvider.getTable("new");
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("getTable", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals("new", args.getString(0));
        Assert.assertTrue(!parser.isNull("returnValue"));
        String fold = folder.getRoot().getPath();
        String dir = new File(fold, "new").toString();
        Assert.assertEquals(String.format("ThreadSafeDatabaseTable[%s]", dir),
                parser.get("returnValue").toString());
        try {
            provider.removeTable("new");
        } catch (IOException e) {
            //
        }
        stringWriter.flush();
    }

    @Test
    public void removeTableLogTest() throws JSONException {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        try {
            provider.createTable("new", list);
        } catch (IOException e) {
            //
        }
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        try {
            wrappedProvider.removeTable("new");
        } catch (IOException e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("removeTable", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals("new", args.getString(0));
        Assert.assertTrue(parser.isNull("returnValue"));
        stringWriter.flush();
    }

    @Test
    public void serializeTableLogTest() throws JSONException {
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedProvider.serialize(table, makeStoreable(5));
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("serialize", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        String fold = folder.getRoot().getPath();
        String dir = new File(fold, "singleColumnTable").toString();
        Assert.assertEquals(String.format("ThreadSafeDatabaseTable[%s]", dir), args.getString(0));
        Assert.assertEquals(makeStoreable(5).toString(), args.getString(1));
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals("<row><col>5</col></row>", parser.getString("returnValue"));
        stringWriter.flush();
    }

    @Test
    public void deserializeTableLogTest() throws JSONException {
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        try {
            wrappedProvider.deserialize(table, "<row><col>5</col></row>");
        } catch (ParseException e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("deserialize", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        String fold = folder.getRoot().getPath();
        String dir = new File(fold, "singleColumnTable").toString();
        Assert.assertEquals(String.format("ThreadSafeDatabaseTable[%s]", dir), args.getString(0));
        Assert.assertEquals("<row><col>5</col></row>", args.getString(1));
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals("DatabaseTableRow[5]", parser.getString("returnValue"));
        stringWriter.flush();
    }

    @Test
    public void createForLogTest() throws JSONException {
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedProvider.createFor(table);
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("createFor", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        String fold = folder.getRoot().getPath();
        String dir = new File(fold, "singleColumnTable").toString();
        Assert.assertEquals(String.format("ThreadSafeDatabaseTable[%s]", dir), args.getString(0));
        List<Class<?>> list = new ArrayList<Class<?>>();
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals("DatabaseTableRow" + list.toString(), parser.get("returnValue"));
        stringWriter.flush();
    }

    @Test
    public void createForSeveralColumnsTableLogTest() throws JSONException {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        wrappedProvider.createFor(table, list);
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("createFor", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        String fold = folder.getRoot().getPath();
        String dir = new File(fold, "singleColumnTable").toString();
        Assert.assertEquals(String.format("ThreadSafeDatabaseTable[%s]", dir), args.getString(0));
        Assert.assertEquals(list.get(0).toString(), args.getJSONArray(1).get(0));
        Assert.assertTrue(!parser.isNull("returnValue"));
        Assert.assertEquals("DatabaseTableRow" + list.toString(), parser.get("returnValue"));
        stringWriter.flush();
    }

    @Test
    public void getExceptionLogTest() throws JSONException {
        TableProvider wrappedProveder = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        try {
            wrappedProveder.getTable(null);
        } catch (IllegalArgumentException e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertEquals(parser.get("thrown"), "java.lang.IllegalArgumentException: Table's name cannot be empty");
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider");
        Assert.assertEquals(parser.getString("method"), "getTable");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertTrue(args.isNull(0));
        Assert.assertEquals(parser.get("thrown"), "java.lang.IllegalArgumentException: Table's name cannot be empty");
        stringWriter.flush();
    }

    @Test
    public void anotherGetColumnTypeExceptionLogTest() throws JSONException {
        Table wrappedTable = (Table) wrapper.wrap(stringWriter, table, Table.class);
        long timestampBefore = System.currentTimeMillis();
        try {
            wrappedTable.getColumnType(1);
        } catch (IndexOutOfBoundsException e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertEquals(parser.get("thrown"), "java.lang.IndexOutOfBoundsException: Index: 1, Size: 1");
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(parser.getString("class"),
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTable");
        Assert.assertEquals(parser.getString("method"), "getColumnType");
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals(args.getInt(0), 1);
        Assert.assertTrue(parser.isNull("returnValue"));
        stringWriter.flush();
    }

    @Test
    public void removeTableExceptionLogTest() throws JSONException {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Integer.class);
        try {
            provider.createTable("new", list);
        } catch (IOException e) {
            //
        }
        TableProvider wrappedProvider = (TableProvider) wrapper.wrap(stringWriter,
                provider, TableProvider.class);
        long timestampBefore = System.currentTimeMillis();
        try {
            wrappedProvider.removeTable("newTable");
        } catch (IOException e) {
            //
        } catch (IllegalStateException e) {
            //
        }
        long timestampAfter = System.currentTimeMillis();
        String result = stringWriter.toString();
        JSONObject parser = new JSONObject(result);
        Assert.assertEquals(parser.get("thrown"), "java.lang.IllegalStateException: newTable not exists");
        Assert.assertTrue(parser.getLong("timestamp") >= timestampBefore
                && parser.getLong("timestamp") <= timestampAfter);
        Assert.assertEquals(
                "ru.fizteh.fivt.students.SurkovaEkaterina.Proxy.TableSystem.ThreadSafeDatabaseTableProvider",
                parser.getString("class"));
        Assert.assertEquals("removeTable", parser.getString("method"));
        JSONArray args = parser.getJSONArray("arguments");
        Assert.assertTrue(args instanceof JSONArray);
        Assert.assertEquals("newTable", args.getString(0));
        Assert.assertTrue(parser.isNull("returnValue"));
        stringWriter.flush();
    }
}
