package ru.fizteh.fivt.students.VasilevKirill.telnet;


import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Shell;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Kirill on 07.12.2014.
 */
public class ClientMain {
    public static void main(String[] args) {
        try {
            Status status = new Status(null);
            Map<String, Command> commands = new HashMap<String, Command>();
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
}
