package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

import java.util.List;

public class ListCommand extends Command {
    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            List<String> list = ((MyStoreableTable) base.getUsing()).list();
            StringBuilder result = new StringBuilder();
            for (String key: list) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(key);
            }
            System.out.println(result.toString());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
