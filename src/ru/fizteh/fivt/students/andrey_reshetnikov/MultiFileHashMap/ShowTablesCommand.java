package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;
import java.util.Map;

public class ShowTablesCommand extends Command {

    @Override
    public void execute(DataBaseOneDir base) throws Exception {
        for (Map.Entry<String, Table> i: base.tables.entrySet()) {
            String name = i.getKey();
            int size = i.getValue().recordsNumber();
            System.out.println(name + " " + size);
        }
    }
}
