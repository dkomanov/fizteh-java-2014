package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CommandDescribe extends CommandTableProvider {
    public CommandDescribe() {
        name = "describe";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        try {
            Table requiredTable = myMap.getTable(args[1]);
            List<Class<?>> types = new ArrayList<>();
            for (int i = 0; i < requiredTable.getColumnsCount(); ++i) {
                types.add(requiredTable.getColumnType(i));
            }
            output.println(TypesUtils.toFileSignature(types));
            return true;
        } catch (IllegalArgumentException e) {
            output.println("there isn't this table in database");
            return false;
        }
    }
}
