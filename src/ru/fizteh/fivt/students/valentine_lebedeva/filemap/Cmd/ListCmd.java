package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class ListCmd implements Cmd {
    public final String getName() {
        return "list";
    }
    public final void execute(final DB dataBase, final String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        List<String> out = new ArrayList<>(dataBase.getBase().size());
        Set<String> keys = dataBase.getBase().keySet();
        for (String key : keys) {
        out.add(key);
        }
        String joined = String.join(", ", out);
        System.out.println(joined);
    }
}
