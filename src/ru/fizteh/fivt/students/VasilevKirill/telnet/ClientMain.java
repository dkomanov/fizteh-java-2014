package ru.fizteh.fivt.students.VasilevKirill.telnet;


import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Shell;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ClientCommands.CreateCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ClientCommands.DropCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ClientCommands.ShowTablesCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ClientCommands.UseCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ConnectCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.DisconnectCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.HandleCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.WhereamiCommand;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Kirill on 07.12.2014.
 */
public class ClientMain {
    public static void main(String[] args) {
        try {
            Status status = new Status(null);
            Map<String, Command> commands = fulfilCommandMap();
            int retValue = 0;
            if (args.length == 0) {
                new Shell(commands, status).handle(System.in);
            } else {
                retValue = new Shell(commands, status).handle(args);
            }
            System.exit(retValue);
        } catch (Exception e) {
            if (e.getMessage().equals("")) {
                System.out.println(e);
            } else {
                System.out.println(e.getMessage());
            }
            System.exit(-1);
        }
    }

    public static Map<String, Command> fulfilCommandMap() {
        Map<String, Command> commands = new HashMap<String, Command>();
        commands.put(new ConnectCommand().toString(), new ConnectCommand());
        commands.put(new DisconnectCommand().toString(), new DisconnectCommand());
        commands.put(new WhereamiCommand().toString(), new WhereamiCommand());
        commands.put("put", new HandleCommand());
        commands.put("get", new HandleCommand());
        commands.put("remove", new HandleCommand());
        commands.put("size", new HandleCommand());
        commands.put("rollback", new HandleCommand());
        commands.put("commit", new HandleCommand());
        commands.put("list", new HandleCommand());
        commands.put(new CreateCommand().toString(), new CreateCommand());
        commands.put(new DropCommand().toString(), new DropCommand());
        commands.put(new ShowTablesCommand().toString(), new ShowTablesCommand());
        commands.put(new UseCommand().toString(), new UseCommand());
        return commands;
    }
}
