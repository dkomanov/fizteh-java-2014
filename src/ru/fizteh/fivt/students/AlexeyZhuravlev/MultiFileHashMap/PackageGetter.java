package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author AlexeyZhuravlev
 */
public class PackageGetter implements CommandGetter {
    AbstractQueue<String> commands;

    public PackageGetter(String[] args) {
        commands = new ConcurrentLinkedQueue<>();
        StringBuilder allCommands = new StringBuilder();
        for (String s: args) {
            allCommands.append(s);
            allCommands.append(' ');
        }
        commands.addAll(Arrays.asList(allCommands.toString().split(";\\s*")));
    }

    @Override
    public String nextCommand() {
        if (commands.isEmpty()) {
            return "exit";
        } else {
            return commands.remove();
        }
    }
}
