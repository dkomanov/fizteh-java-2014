package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class ListCommand extends Command {
    public final void execute(final DB dataBase, final String[] args) {
        checkArgs(1, args);
        List<String> out = new ArrayList<>(dataBase.getBase().size());
        Set<String> keys = dataBase.getBase().keySet();
        for (String key : keys) {
            out.add(key);
        }
        System.out.println(String.join(", ", out));
    }
}
