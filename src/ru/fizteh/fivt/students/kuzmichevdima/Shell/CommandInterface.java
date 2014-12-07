package ru.fizteh.fivt.students.kuzmichevdima.shell.src;

/**
 * Created by kuzmi_000 on 20.10.14.
 */
public interface CommandInterface {
    void apply(final String [] args, final CurrentDir dir);
}
