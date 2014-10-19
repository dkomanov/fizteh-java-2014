package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import java.util.Arrays;
import java.util.ArrayDeque;


public class BatchMode implements CommandMode {
    ArrayDeque<String> commands;

    BatchMode(String[] arguments) {
        commands = new ArrayDeque<>();
        StringBuilder allCommands = new StringBuilder();
        for (String s : arguments) {
            allCommands.append(s);
            allCommands.append(' ');
        }
        commands.addAll(Arrays.asList(allCommands.toString().split(";")));
    }

    @Override
    public String runInterpreterCycle() {
        if (commands.isEmpty()) {
            return "exit";
        } else {
            return commands.remove();
        }
    }
}

