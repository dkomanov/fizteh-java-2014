package ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap;

import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Command;
import ru.fizteh.fivt.students.VasilevKirill.Storeable.junit.multimap.db.shell.Status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 19.10.2014.
 */
public class CreateCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("Create: Wrong arguments");
        }
        if (args[2].charAt(0) != '(') {
            throw new IOException("Create: can't find brackets");
        } else {
            args[2] = args[2].substring(1);
        }
        int last = args.length - 1;
        if (args[last].charAt(args[last].length() - 1) != ')') {
            throw new IOException("Create: can't find brackets");
        } else {
            args[last] = args[last].substring(0, args[last].length() - 1);
        }
        List<Class<?>> typeList = new ArrayList<>();
        try {
            for (int i = 2; i < args.length; ++i) {
                typeList.add(Class.forName(args[i]));
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Create: unknown class");
        }
        if (status.getMultiMap().createTable(args[1], typeList) != null) {
            System.out.println("created");
        } else {
            System.out.println(args[1] + " exists");
        }
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null || args[1] == null) {
            return false;
        }
        return true;
        //return args.length == 2;
    }

    @Override
    public String toString() {
        return "create";
    }
}
