package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;


import java.io.PrintStream;
import java.util.List;

public class CommandListDistribute extends CommandTableProviderExtended {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended dataBase, String[] args, PrintStream output) {
        Table currentTable = dataBase.getTable(args[1]);
        if (currentTable == null) {
            output.println("there isn't table \"" + args[1] + "\" on server");
            return false;
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
