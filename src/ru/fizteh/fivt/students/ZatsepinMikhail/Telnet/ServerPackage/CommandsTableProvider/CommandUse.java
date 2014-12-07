package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;

import java.io.PrintStream;

public class CommandUse extends CommandTableProvider {
    public CommandUse() {
        name = "use";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        FileMap newCurrentTable = (FileMap) myMap.getTable(args[1]);
        if (newCurrentTable != null) {
            FileMap currentTable = myMap.getCurrentTable();
            if (currentTable == null) {
                myMap.setCurrentTable(newCurrentTable);
                output.println("using " + args[1]);
            } else if (currentTable.getNumberOfUncommittedChanges() > 0) {
                output.println(currentTable.getNumberOfUncommittedChanges() + " unsaved changes");
            } else {
                myMap.setCurrentTable(newCurrentTable);
                output.println("using " + args[1]);
            }
        } else {
            output.println(args[1] + " not exists");
        }
        return true;
    }
}
