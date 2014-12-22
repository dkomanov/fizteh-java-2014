package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;


import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.MultiListCommand;

public class JUnitListCommand extends JUnitCommand {
    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        Command list = new MultiListCommand();
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            list.executeOnTable(base.getUsing().getDirtyTable());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
