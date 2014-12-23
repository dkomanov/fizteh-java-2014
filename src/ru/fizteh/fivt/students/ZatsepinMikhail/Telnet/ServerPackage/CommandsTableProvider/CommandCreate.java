package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class CommandCreate extends CommandTableProviderExtended {
    public CommandCreate() {
        name = "create";
        numberOfArguments = -1;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        StringBuilder simpleBuilder = new StringBuilder();
        for (int i = 2; i < args.length; ++i) {
            if (i != 2) {
                simpleBuilder.append(" ");
            }
            simpleBuilder.append(args[i]);
        }
        String concatArgs = simpleBuilder.toString();
        if (concatArgs.lastIndexOf("(") != 0
                || concatArgs.indexOf(")") != concatArgs.length() - 1) {
            output.println("types should be in format: \"<name> (type1 type2 ... typeN)\"");
            return false;
        }

        concatArgs = new String(concatArgs.toCharArray(), 1, concatArgs.length() - 2);
        List<Class<?>> types;
        try {
            types = TypesUtils.toTypeList(concatArgs.split("\\s+"));
        } catch (ColumnFormatException e) {
            output.println(e.getMessage());
            return false;
        }
        try {
            if (myMap.createTable(args[1], types) != null) {
                output.println("created");
            } else {
                output.println("\'" + args[1] + "\' already exists");
            }
        } catch (IOException e) {
            return false;
        } catch (IllegalArgumentException e) {
            output.println(e.getMessage());
        }
        return true;
    }
}
