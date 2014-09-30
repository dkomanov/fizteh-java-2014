package ru.fizteh.fivt.students.sautin1.shell;

/**
 * Created by sautin1 on 9/30/14.
 */
public interface Command {
    public void execute(String... args);
    public String toString();
}
