package ru.fizteh.fivt.students.sautin1.shell;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 * Created by sautin1 on 9/30/14.
 */
public interface Command {
    public void execute(String... args) throws IllegalArgumentException;
    public String toString();
}
