package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import java.util.Arrays;
import java.util.AbstractQueue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class BatchMode implements CommandMode {
    AbstractQueue<String> commands;

    BatchMode(String[] arguments) {
        commands = new ConcurrentLinkedQueue<>();
        StringBuilder allCommands = new StringBuilder();
        for (String s : arguments) {
            allCommands.append(s);
            allCommands.append(' ');
        }
        commands.addAll(Arrays.asList(allCommands.toString().split(";")));
    }

    @Override
    public String mainAimOfWork() {
        if (commands.isEmpty()) {
            return "exit";
        } else {
            return commands.remove();
        }
    }
}

