package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.MultiFileHashMap;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.StoreablePackage.TypesUtils;

import java.io.IOException;
import java.util.List;

public class CommandCreate extends CommandMultiFileHashMap {
    public CommandCreate() {
        name = "create";
        numberOfArguments = -1;
    }

    @Override
    public boolean run(MFileHashMap myMultiDataBase, String[] args) {
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
            System.out.println("types should be in format: \"<name> (type1 type2 ... typeN)\"");
            return false;
        }

        concatArgs = new String(concatArgs.toCharArray(), 1, concatArgs.length() - 2);
        List<Class<?>> types;
        try {
            types = TypesUtils.toTypeList(concatArgs.split("\\s+"));
        } catch (ColumnFormatException e) {
            System.out.println(e.getMessage());
            return false;
        }
        try {
            myMultiDataBase.createTable(args[1], types);
            System.out.println("created");
        } catch (IOException e) {
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
}
