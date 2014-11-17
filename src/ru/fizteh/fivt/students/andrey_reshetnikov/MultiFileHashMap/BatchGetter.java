package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BatchGetter implements CommandGetter {
    AbstractQueue<String> activeCommands;

    BatchGetter(String[] args) {
        activeCommands = new ConcurrentLinkedQueue<>();
        StringBuilder allCommands = new StringBuilder();
        for (String s: args) {
            allCommands.append(s);
            allCommands.append(' ');
        }
        activeCommands.addAll(Arrays.asList(allCommands.toString().split(";")));
    }

    @Override
    public String nextCommand() {
        if (activeCommands.isEmpty()) {
            return "exit";
        } else {
            return activeCommands.remove();
        }
    }
}
