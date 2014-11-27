package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.MultiPutCommand;

public class JUnitPutCommand extends JUnitCommand {

    private String key;
    private String value;

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
