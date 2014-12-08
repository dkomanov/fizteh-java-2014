package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;


import java.io.PrintStream;
import java.util.List;

public class CommandListDistribute extends CommandTableProviderExtended {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        Table currentTable = myMap.getCurrentTable();
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
