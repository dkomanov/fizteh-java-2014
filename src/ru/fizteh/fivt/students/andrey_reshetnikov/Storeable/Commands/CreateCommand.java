package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.TypeTransformer;

import java.io.IOException;
import java.util.List;

public class CreateCommand extends Command {

    private List<Class<?>> types;
    private String name;

    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
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
        String s = args[2].substring(1, args[2].length() - 1).replaceAll("`", " ");
        try {
            types = TypeTransformer.classListFromString(s);
        } catch (IOException e) {
            throw new Exception("wrong type (" + e.getMessage() + ")");
        }
    }
}
