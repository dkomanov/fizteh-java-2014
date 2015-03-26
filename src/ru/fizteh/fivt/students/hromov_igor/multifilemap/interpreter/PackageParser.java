package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.commands.ParentCommand;

import java.util.ArrayList;
import java.util.HashMap;

public class PackageParser {

    public static void run(HashMap<String, ParentCommand> listCommands, String[] arg) {
        try {
            ArrayList<String> current = new ArrayList<String>();
            for (int i = 0; i < arg.length; ++i) {
                current.clear();
                while (i < arg.length) {
                    if (!(arg[i].contains(";"))) {
                        current.add(arg[i]);
                        i++;
                    } else {
                        current.add(arg[i].substring(0, arg[i].indexOf(";")));
                        break;
                    }
                }
                if (current.size() == 0) {
                    return;
                }
                String[] commands = new String[current.size()];
                commands = current.toArray(commands);
                Parser.parseCommand(commands, listCommands);
            }
        } catch (IllegalMonitorStateException e) {
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.err.print("Wrong arguments" + e);
        }
    }
}
