package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.MultiRemoveCommand;

/**
 * @author AlexeyZhuravlev
 */
public class JUnitRemoveCommand extends JUnitCommand {

    String key;

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
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;
            if (base.getUsing().dirtyTable.databases[dir][file].data.get(key) != null) {
                remove.executeOnTable(base.getUsing().dirtyTable);
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
