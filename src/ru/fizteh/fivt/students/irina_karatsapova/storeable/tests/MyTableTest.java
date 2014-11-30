package ru.fizteh.fivt.students.irina_karatsapova.storeable.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions.ColumnFormatException;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.exceptions.TableException;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Storeable;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.Table;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.table_provider_factory.MyTableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.table_provider_factory.MyTableRaw;
import ru.fizteh.fivt.students.irina_karatsapova.storeable.utils.Utils;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MyTableTest {

    String providerDir = "d://tmp-storeable-test";
    Table table;
    String oneMoreTableName = "one-more-table-name";
    TableProvider provider;
    Class[] classes = {Integer.class, String.class};
    List<Class<?>> types = new ArrayList<>();
    String[] xmlStr = {"<row><col>5</col><col>str0</col></row>",
                        "<row><col>4</col><col>str1</col></row>",
                        "<row><col>3</col><col>str2</col></row>",
                        "<row><col>2</col><col>str3</col></row>",
                        "<row><col>1</col><col>str4</col></row>",
                        "<row><col>0</col><col>str5</col></row>", };

    @Before
    public void setUp() throws Exception {
        TableProviderFactory factory = new MyTableProviderFactory();
        provider = factory.create(providerDir);
        for (Class type: classes) {
            types.add(type);
        }
        table = provider.createTable(oneMoreTableName, types);
    }

    @After
    public void tearDown() throws Exception {
        File providerDirFile = Paths.get(providerDir).toFile();
        Utils.rmdirs(providerDirFile);
    }

    @Test
    public void testGoodTableGetName() throws Exception {
        String[] goodTableNames = {"table", "name", "123", "db № 4 with spaces", "null", "1.a"};
        for (String tableName : goodTableNames) {
            Table goodTable = provider.createTable(tableName, types);
            assertEquals(tableName, goodTable.getName());
        }
    }

    @Test
    public void testBadTableGetName() throws Exception {
        String[] badTableNames = {"..", "s//ym//b//ols", "enter/n"};
        int exceptionsNumber = 0;
        for (String tableName : badTableNames) {
            try {
                Table badTable = provider.createTable(tableName, types);
                badTable.getName();
            } catch (TableException e) {
                exceptionsNumber++;
            }
        }
        assertEquals(badTableNames.length, exceptionsNumber);
    }

    @Test
    public void testGet() throws Exception {
        table.put("1", deserialize(xmlStr[0]));
        table.put("2", deserialize(xmlStr[1]));
        assertEquals(xmlStr[0], serialize(table.get("1")));
        table.put("1", deserialize(xmlStr[2]));
        assertEquals(xmlStr[2], serialize(table.get("1")));
        assertEquals(xmlStr[1], serialize(table.get("2")));
        table.remove("1");
        assertNull(table.get("1"));
        assertNull(table.get("3"));
        table.put("1", deserialize(xmlStr[0]));
        assertEquals(xmlStr[0], serialize(table.get("1")));
    }

    @Test
    public void testPut() throws Exception {
        assertNull(table.put("1", deserialize(xmlStr[0])));
        assertEquals(xmlStr[0], serialize(table.put("1", deserialize(xmlStr[1]))));
        table.put("2", deserialize(xmlStr[0]));
        table.remove("2");
        assertNull(table.put("2", deserialize(xmlStr[3])));
        assertEquals(xmlStr[1], serialize(table.get("1")));
        assertEquals(xmlStr[1], serialize(table.put("1", deserialize(xmlStr[5]))));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testPutMoreThanExpected() throws Exception {
        table.put("1", deserialize("<row><col>5</col><col>str</col><col>one-more-str</col></row>"));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testPutLessThanExpected() throws Exception {
        table.put("1", deserialize("<row><col>5</col></row>"));
    }

    @Test(expected = ColumnFormatException.class)
    public void testPutValuesWithWrongTypes() throws Exception {
        Class[] wrongClasses = {String.class, Integer.class};
        List<Class<?>> wrongTypes = new ArrayList<>();
        for (Class type: wrongClasses) {
            wrongTypes.add(type);
        }
        MyTableRaw tableRaw = new MyTableRaw(wrongTypes);
        tableRaw.setColumnAt(0, "hello");
        tableRaw.setColumnAt(1, 5);
        table.put("1", tableRaw);
    }

    @Test
    public void testRemove() throws Exception {
        assertNull(table.remove("1"));
        table.put("1", deserialize(xmlStr[0]));
        assertEquals(xmlStr[0], serialize(table.remove("1")));
        assertNull(table.remove("1"));
        table.put("2", deserialize(xmlStr[1]));
        table.put("2", deserialize(xmlStr[4]));
        assertEquals(xmlStr[4], serialize(table.remove("2")));
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(0, table.size());
        table.put("1", deserialize(xmlStr[0]));
        assertEquals(1, table.size());
        table.put("2", deserialize(xmlStr[3]));
        table.put("3", deserialize(xmlStr[0]));
        assertEquals(3, table.size());
        table.remove("2");
        assertEquals(2, table.size());
        table.remove("4");
        assertEquals(2, table.size());
    }

    @Test
    public void testCommit() throws Exception {
        table.put("1", deserialize(xmlStr[0]));
        assertEquals(1, table.commit());
        assertEquals(1, table.size());
        table.put("1", deserialize(xmlStr[3]));
        table.remove("1");
        assertEquals(2, table.commit());
        table.put("1", deserialize(xmlStr[4]));
        table.put("2", deserialize(xmlStr[3]));
        table.put("3", deserialize(xmlStr[4]));
        table.put("3", deserialize(xmlStr[5]));
        assertEquals(4, table.commit());
        table.get("1");
        table.get("4");
        table.remove("2");
        assertEquals(1, table.commit());

        Table sameTable = provider.getTable(oneMoreTableName);
        assertEquals(2, sameTable.size());
        assertEquals(xmlStr[4], serialize(sameTable.get("1")));
        assertEquals(xmlStr[5], serialize(sameTable.get("3")));
        assertNull(sameTable.get("2"));
        assertNull(sameTable.get("4"));
    }

    @Test
    public void testRollback() throws Exception {
        table.put("1", deserialize(xmlStr[0]));
        assertEquals(1, table.rollback());
        assertEquals(0, table.size());
        table.put("1", deserialize(xmlStr[3]));
        table.remove("1");
        assertEquals(2, table.rollback());
        assertEquals(0, table.size());
        table.put("1", deserialize(xmlStr[4]));
        table.put("2", deserialize(xmlStr[3]));
        table.put("3", deserialize(xmlStr[4]));
        table.put("3", deserialize(xmlStr[5]));
        assertEquals(4, table.rollback());
        table.get("1");
        table.get("4");
        table.remove("2");
        assertEquals(0, table.rollback());
        assertEquals(0, table.size());
        assertNull(table.get("1"));
        assertNull(table.get("3"));
    }

    @Test
    public void testChangesNumber() throws Exception {
        assertEquals(0, table.getNumberOfUncommittedChanges());
        table.put("1", deserialize(xmlStr[0]));
        assertEquals(1, table.getNumberOfUncommittedChanges());
        table.commit();
        assertEquals(0, table.getNumberOfUncommittedChanges());
        table.put("1", deserialize(xmlStr[3]));
        table.remove("1");
        table.put("2", deserialize(xmlStr[3]));
        assertEquals(3, table.getNumberOfUncommittedChanges());
        table.rollback();
        assertEquals(0, table.getNumberOfUncommittedChanges());
        table.get("2");
        table.get("1");
        table.size();
        assertEquals(0, table.getNumberOfUncommittedChanges());
    }

    @Test
    public void testList() throws Exception {
        String[] keys = {"table", "name", "123", "db № 4 with spaces", "null", "1.a"};
        for (String key : keys) {
            table.put(key, deserialize(xmlStr[0]));
        }
        List<String> tableKeys = table.list();
        assertEquals(keys.length, tableKeys.size());
        for (String key : keys) {
            assertTrue(tableKeys.contains(key));
        }
    }

    @Test
    public void testGetColumnsType() throws Exception {
        assertEquals(Integer.class, table.getColumnType(0));
        assertEquals(String.class, table.getColumnType(1));
    }
    
    private Storeable deserialize(String str) throws Exception {
        return provider.deserialize(table, str);
    }

    private String serialize(Storeable stor) throws Exception {
        return provider.serialize(table, stor);
    }
}
