package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import java.util.Map;

/**
 * @author AlexeyZhuravlev
 */
public class ShowTablesCommand extends Command {

    protected int numberOfArguments() {
        return 0;
    }

    @Override
    public void execute(DataBaseDir base) throws Exception {
        for (Map.Entry<String, MultiTable> entry: base.tables.entrySet()) {
            String name = entry.getKey();
            int size = entry.getValue().recordsNumber();
            System.out.println(name + " " + size);
        }
    }
}
