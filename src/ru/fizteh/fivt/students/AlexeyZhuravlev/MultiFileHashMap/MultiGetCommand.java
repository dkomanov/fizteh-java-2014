package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexeyZhuravlev.filemap.DataBase;
import ru.fizteh.fivt.students.AlexeyZhuravlev.filemap.GetCommand;

/**
 * @author AlexeyZhuravlev
 */
public class MultiGetCommand extends Command {
    private String key;

    protected void putArguments(String[] args) {
        key = args[1];
    }

    protected int numberOfArguments() {
        return 1;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            GetCommand get = new GetCommand(key);
            DataBase db = base.getUsing().databases[dir][file];
            if (db == null) {
                System.out.println("not found");
            } else {
                get.execute(base.getUsing().databases[dir][file]);
            }
        }
    }
}
