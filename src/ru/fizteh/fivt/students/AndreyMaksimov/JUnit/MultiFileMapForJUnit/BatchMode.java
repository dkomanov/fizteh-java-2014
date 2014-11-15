package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BatchMode implements CommandGetter {
    AbstractQueue<String> commands;

    public BatchMode(String[] args) {
        commands = new ConcurrentLinkedQueue<>();
        StringBuilder allCommands = new StringBuilder();
        for (String needString: args) {
            allCommands.append(needString);
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

