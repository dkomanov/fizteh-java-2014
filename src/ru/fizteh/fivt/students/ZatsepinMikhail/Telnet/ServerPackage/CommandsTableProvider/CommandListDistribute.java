package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;

import java.io.PrintStream;
import java.util.List;

public class CommandListDistribute extends CommandTableProvider {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            output.println("no table");
            return true;
        }
        List<String> allKeys = currentTable.list();
        StringBuilder newMessage = new StringBuilder();
        int counter = 0;
        for (String oneKey : allKeys) {
            if (counter > 0) {
                newMessage.append(", ");
            }
            newMessage.append(oneKey);
            ++counter;
        }
        output.println(newMessage.toString());
        return true;
    }
}
