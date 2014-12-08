package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;


import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BatchGetter implements CommandGetter {
    private AbstractQueue<String> commands;

    public BatchGetter(String[] args) {
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
