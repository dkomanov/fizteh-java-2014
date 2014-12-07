package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;


import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Command;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.PutMulti;

public class JUnitPut extends JUnitCommand {

    String key;
    String value;

    public JUnitPut(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    public JUnitPut() {}

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command put = new PutMulti(key, value);
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
