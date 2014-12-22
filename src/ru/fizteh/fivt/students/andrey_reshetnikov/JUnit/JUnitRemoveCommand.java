package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.ConstClass;
import ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap.MultiRemoveCommand;

public class JUnitRemoveCommand extends JUnitCommand {

    private String key;

    public JUnitRemoveCommand(String passedKey) {
        key = passedKey;
    }

    public JUnitRemoveCommand() {}

    @Override
    public void execute(JUnitDataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            Command remove = new MultiRemoveCommand(key);
            int hashCode = Math.abs(key.hashCode());
            int dir = hashCode % ConstClass.NUM_DIRECTORIES;
            int file = hashCode / ConstClass.NUM_FILES % ConstClass.NUM_FILES;
            if (base.getUsing().getDirtyTable().databases[dir][file].data.get(key) != null) {
                remove.executeOnTable(base.getUsing().getDirtyTable());
                base.getUsing().changes.add(remove);
            } else {
                System.out.println("not found");
            }
        }
    }

    protected final void putArguments(String[] args) {
        key = args[1];
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }
}
