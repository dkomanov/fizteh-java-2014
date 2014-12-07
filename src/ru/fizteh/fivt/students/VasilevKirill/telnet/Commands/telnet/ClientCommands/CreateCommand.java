package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ClientCommands;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyRemoteTableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 07.12.2014.
 */
public class CreateCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        RemoteTableProvider tableProvider = status.getTableProvider();
        if (tableProvider == null) {
            throw new IOException("Can't find the table provider");
        }
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
                if (args[i].charAt(args[i].length() - 1) == ',') {
                    args[i] = args[i].substring(0, args[i].length() - 1);
                }
                switch(args[i]) {
                    case "int":
                        typeList.add(Integer.class);
                        break;
                    case "double":
                        typeList.add(Double.class);
                        break;
                    case "char":
                        typeList.add(Character.class);
                        break;
                    case "long":
                        typeList.add(Long.class);
                        break;
                    case "float":
                        typeList.add(Float.class);
                        break;
                    case "boolean":
                        typeList.add(Boolean.class);
                        break;
                    default:
                        typeList.add(Class.forName("java.lang." + args[i]));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Create: unknown class");
        }
        if (status.getTableProvider().createTable(args[1], typeList) != null) {
            System.out.println("created");
        }
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null || args.length < 3) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "create";
    }
}
