package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public abstract class Command {
    protected String[] args;
    protected FileBase data;
    abstract String name();
    abstract void execute() throws ExceptionStopProcess, ExceptionIncorrectInput;
    protected Command(){}

}
