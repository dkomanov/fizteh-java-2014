package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.TypeTransformer;

import java.io.IOException;
import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class CreateCommand extends Command {

    List<Class<?>> types;
    String name;

    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        Table newtable = base.createTable(name, types);
        if (newtable == null) {
            System.out.println(name + " exists");
        } else {
            System.out.println("created");
        }
    }

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    @Override
    protected void putArguments(String[] args) throws Exception {
        name = args[1];
        if (args[2].charAt(0) != '(' || args[2].charAt(args[2].length() - 1) != ')') {
            throw new Exception("wrong type (second argument of create must be in ())");
        }
        String s = args[2].substring(1, args[2].length() - 2).replaceAll("`", " ");
        try {
            types = TypeTransformer.typeListFromString(s);
        } catch (IOException e) {
            throw new Exception("wrong type (" + e.getMessage() + ")");
        }
    }
}
