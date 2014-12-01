package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Create extends StoreableCommand {
    private static final HashMap<String, Class<?>> TYPES;

    static {
        TYPES = new HashMap<>();
        TYPES.put("int", Integer.class);
        TYPES.put("long", Long.class);
        TYPES.put("byte", Byte.class);
        TYPES.put("float", Float.class);
        TYPES.put("double", Double.class);
        TYPES.put("boolean", Boolean.class);
        TYPES.put("String", String.class);
    }

    public Create() {
        super("create", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkMoreArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (!args[1].startsWith("(") || !args[1].endsWith(")")) {
            System.err.println("wrong arguments: ( ) not found");
            return !batchModeInInteractive;
        }
        java.util.List<Class<?>> types = new ArrayList<>();
        args[1] = args[1].substring(1, args[1].length() - 1);
        try {
            for (String tempName : args[1].split("\\s+", 0)) {
                types.add(classByName(tempName));
            }
            dbConnector.activeTableProvider.createTable(args[0], types);
        } catch (IOException e) {
            System.err.println("Some IOException happen (in createTable)");
            return !batchModeInInteractive;
        }
        return true;
    }

    private static Class<?> classByName(String name) throws IOException {
        if (!TYPES.containsKey(name)) {
            throw new IOException("Unknown type name: " + name);
        }
        return TYPES.get(name);
    }
}
