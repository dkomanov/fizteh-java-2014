package ru.fizteh.fivt.students.Oktosha.Command;

/**
 * A simple class which holds name of command and it's arguments.
 */
public class Command {
    public Command(String commandString) {
        commandString = commandString.trim();
        String[] split = commandString.split("\\s+");
        name = split[0];
        args = new String[split.length - 1];
        System.arraycopy(split, 1, args, 0, split.length - 1);
    }
    public String name;
    public String[] args;
}
