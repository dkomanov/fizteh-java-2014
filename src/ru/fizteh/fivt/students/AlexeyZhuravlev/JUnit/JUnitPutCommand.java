package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.MultiPutCommand;

/**
 * @author AlexeyZhuravlev
 */
public class JUnitPutCommand extends JUnitCommand {

    String key;
    String value;

    public JUnitPutCommand(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    public JUnitPutCommand() {}

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command put = new MultiPutCommand(key, value);
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            put.executeOnTable(base.getUsing().dirtyTable);
            base.getUsing().changes.add(put);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    @Override
    protected void putArguments(String[] args) {
        key = args[1];
        value = args[2];
    }
}
