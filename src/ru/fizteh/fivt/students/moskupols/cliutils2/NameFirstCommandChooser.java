package ru.fizteh.fivt.students.moskupols.cliutils2;

import ru.fizteh.fivt.students.moskupols.cliutils2.commands.Command;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.NameFirstCommand;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by moskupols on 02.12.14.
 */
public class NameFirstCommandChooser implements CommandChooser {
    final Map<String, NameFirstCommand> namesMap;

    public NameFirstCommandChooser(NameFirstCommand... commands) {
        namesMap = new HashMap<>();
        for (NameFirstCommand command : commands) {
            if (namesMap.put(command.name(), command) != null) {
                throw new IllegalArgumentException(
                        "Commands' names must differ, but " + command.name() + "repeats");
            }
        }
    }

    @Override
    public Command commandForArgs(String[] args) {
        return namesMap.get(args[0]);
    }
}
