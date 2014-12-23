package ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ClientCommands;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kirill on 07.12.2014.
 */
public class ShowTablesCommand implements Command {
    @Override
    public int execute(String[] args, Status status) throws IOException {
        if (!checkArgs(args)) {
            throw new IOException("Wrong arguments");
        }
        List<String> tables = status.getTableProvider().getTableNames();
        for (String table : tables) {
            System.out.println(table);
        }
        return 0;
    }

    @Override
    public boolean checkArgs(String[] args) {
        if (args == null) {
            return false;
        }
        return args.length == 2;
    }

    @Override
    public String toString() {
        return "show";
    }
}
