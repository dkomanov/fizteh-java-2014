package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public interface CommandProcess{
    void process(Container commandFromString) throws ExceptionUnknownCommand;
}
