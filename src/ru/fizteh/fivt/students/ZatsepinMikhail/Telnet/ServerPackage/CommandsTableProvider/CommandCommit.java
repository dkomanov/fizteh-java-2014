package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;

import java.io.IOException;
import java.io.PrintStream;

public class CommandCommit extends CommandTableProvider {
    public CommandCommit() {
        name = "commit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            output.println("no table");
        } else {
            try {
                output.println(currentTable.commit());
            } catch (IOException e) {
                output.println("io exception while writing in file");
                return false;
            }
        }
        return true;
    }
}
