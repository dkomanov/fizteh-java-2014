package ru.fizteh.fivt.students.egor_belikov.Parallel.Commands;

import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTable;
import ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider;

import java.util.ArrayList;
import java.util.List;

import static ru.fizteh.fivt.students.egor_belikov.Parallel.MySerializer.returningClass;
import static ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider.listOfTables;

/**
 * Created by egor on 13.12.14.
 */
public class Create implements Command {
    public void execute(String args[], MyTableProvider myTableProvider) throws Exception {
        String stringTypes = "";
        for (int i = 2; i != args.length; i++) {
            stringTypes = stringTypes + " " + args[i];
        }
        stringTypes = stringTypes.trim();
        if (stringTypes.length() < 3
                || stringTypes.charAt(0) != '('
                || stringTypes.charAt(stringTypes.length() - 1) != ')') {
            throw new IllegalArgumentException("wrong types (signature)");
        }
        List<Class<?>> signature = new ArrayList<>();
        String[] types = stringTypes.substring(1, stringTypes.length() - 1).split("\\s+");
        for (String type : types) {
            if (type.trim().isEmpty()) {
                throw new Exception("wrong types (signature)");
            }
            Class<?> c = returningClass(type.trim());
            if (c == null) {
                throw new Exception("wrong type (" + type.trim() + " is not a valid type name)");
            }
            signature.add(c);
        }
        if (types.length == 0) {
            throw new Exception("wrong type (empty type is not allowed)");
        }
        MyTable t = (MyTable) myTableProvider.createTable(args[1], signature);
        t.writeSignature();
        if (t != null) {
            System.out.println("created");
            listOfTables.put(args[1], t);
        } else {
            System.out.println(args[1] + " exists");
        }
    }
}
