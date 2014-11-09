package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.StoreablePackage.TypesUtils;

import java.io.IOException;
import java.util.List;

public class CommandCreate extends CommandMultiFileHashMap {
    public CommandCreate() {
        name = "create";
        numberOfArguments = -1;
    }

    @Override
    public boolean run(MFileHashMap myMultiDataBase, String[] args) {
        if (args[2].charAt(0) != '(' || !args[args.length - 1].endsWith(")")) {
            System.out.println(name + ": wrong arguments");
            return false;
        }
        List<Class<?>> types = TypesUtils.toTypeList(args, true);
        try {
            myMultiDataBase.createTable(args[1], types);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
