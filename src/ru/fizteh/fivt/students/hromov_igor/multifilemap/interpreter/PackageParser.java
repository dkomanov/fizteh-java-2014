package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.exception.ExitCommandException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PackageParser {

    public static void run(HashMap<String, BaseCommand> listCommands, String[] arg) {
        try {
            List<String> current = new ArrayList<String>();
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
                String[] commands = current.toArray(new String[current.size()]);
                if (commands[0].equals("show")) {
                    commands[0] += "_" + commands[1];
                }
                Parser.parseAndExecute(commands, listCommands);
            }
        } catch (ExitCommandException e) {
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.err.print("Wrong arguments" + e);
        }
    }
}
