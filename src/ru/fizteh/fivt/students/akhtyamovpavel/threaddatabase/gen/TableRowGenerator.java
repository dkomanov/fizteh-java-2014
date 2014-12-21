package ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.gen;

import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTable;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.DataBaseTableProvider;
import ru.fizteh.fivt.students.akhtyamovpavel.threaddatabase.TableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

/**
 * Created by user1 on 15.11.2014.
 */
public class TableRowGenerator {
    static Random random = new Random();
    private static HashMap<Integer, Class<?>> types;
    private static HashMap<Class<?>, Generator> randomGenerators = new HashMap<>();

    public static Class<?> generateType() {
        int type = Math.abs(random.nextInt()) % 7;
        return types.get(type);
    }

    public static ArrayList<Class<?>> generateSignature() {
        ArrayList<Class<?>> signature = new ArrayList<>();
        for (int i = 0; i < 50; ++i) {
            signature.add(generateType());
        }
        return signature;
    }

    public static ArrayList<Object> generateValues(ArrayList<Class<?>> signature) {
        ArrayList<Object> values = new ArrayList<>();
        for (int i = 0; i < 50; ++i) {
            values.add(randomGenerators.get(signature.get(i)).getObject());
        }
        values.set(Math.abs(random.nextInt()) % 50, null);
        return values;
    }

    public static TableRow generateRow(DataBaseTableProvider provider,
                                       DataBaseTable table,
                                       ArrayList<Class<?>> signature
    ) {
        return (TableRow) provider.createFor(table, generateValues(signature));
    }

    interface Generator {
        Object getObject();
    }

    static {
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
}
