package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
public interface Container {
    Command getCommandByName(String s) throws ExceptionUnknownCommand;
}
