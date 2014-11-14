package ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.test;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.TableRow;
import ru.fizteh.fivt.students.akhtyamovpavel.structureddatabase.TableRowSerializer;

import java.io.IOException;
import java.net.InterfaceAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class TableRowSerializerTest {
    private static TableRowSerializer serializer;
    private static DataBaseTableProvider provider;
    private DataBaseTable table;
    private ArrayList<Class<?>> signature;
    private static HashMap<Integer, Class<?>> types;
    private static HashMap<Class<?>, Generator> randomGenerators = new HashMap<>();

    static Random random = new Random();


    Class<?> generateType() {
        int type = Math.abs(random.nextInt()) % 7;
        return types.get(type);
    }

    void generateSignature() {
        signature = new ArrayList<>();
        for (int i = 0; i < 50; ++i) {
            signature.add(generateType());
        }
    }

    ArrayList<Object> generateValues(ArrayList<Class<?>> signature) {
        ArrayList<Object> values = new ArrayList<>();
        for (int i = 0; i < 50; ++i) {
            values.add(randomGenerators.get(signature.get(i)).getObject());
        }
        return values;
    }


    public void assertRows(Storeable expected, Storeable actual) {
        ArrayList<Object> expectedList = new ArrayList<>();
        ArrayList<Object> actualList = new ArrayList<>();
        for (int i = 0; i < table.getColumnsCount(); ++i) {
            expectedList.add(expected.getColumnAt(i));
            actualList.add(actual.getColumnAt(i));
        }
        assertEquals(expectedList, actualList);
    }

    interface Generator {
        Object getObject();
    }

    @BeforeClass
    public static void init() {
        provider = new DataBaseTableProvider("D:\\test\\database3");
        serializer = new TableRowSerializer();
        types = new HashMap<>();
        types.put(0, Integer.class);
        types.put(1, Long.class);
        types.put(2, Float.class);
        types.put(3, Double.class);
        types.put(4, Byte.class);
        types.put(5, String.class);
        types.put(6, Boolean.class);

        randomGenerators.put(Integer.class, random::nextInt);
        randomGenerators.put(Long.class, random::nextLong);
        randomGenerators.put(Float.class, random::nextFloat);
        randomGenerators.put(Double.class, random::nextDouble);
        randomGenerators.put(String.class, UUID.randomUUID()::toString);
        randomGenerators.put(Boolean.class, () -> {
            return random.nextInt() == 1;
        });
        randomGenerators.put(Byte.class, () -> {
            byte[] bytes = new byte[1];
            random.nextBytes(bytes);
            return bytes[0];
        });
    }



    @Before
    public void generateTable() {
        String tableName = UUID.randomUUID().toString();
        generateSignature();
        try {
            table = (DataBaseTable) provider.createTable(tableName, signature);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    @After
    public void releaseTable() {
        provider.removeTable(table.getName());
    }

    @Test
    public void testSerialize(){
        try {
            provider.deserialize(table, "[,,]");
            assertTrue(false);
        } catch (ParseException pe) {
            assertTrue(true);
        }
        try {
            provider.deserialize(table, "[,]");
            assertTrue(false);
        } catch (ParseException pe) {
            assertTrue(true);
        }

        try {
            provider.deserialize(table, "[]");
            assertTrue(false);
        } catch (ParseException pe) {
            assertTrue(true);
        }

        ArrayList<Object> values = generateValues(signature);
        Storeable row = new TableRow(values);
        try {
            assertRows(row, provider.deserialize(table, provider.serialize(table, row)));
        } catch (ParseException pe) {
            System.err.println(pe.getMessage());
            assertTrue(false);
        }



    }





}
