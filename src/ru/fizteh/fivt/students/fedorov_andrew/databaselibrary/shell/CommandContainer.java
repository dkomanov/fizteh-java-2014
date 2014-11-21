package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import java.util.Map;

/**
 * Base interface for class that has a variety of commands suitable for given shell state.
 * @param <State>
 *         Some class extending ShellState
 * @see ShellState
 */
public interface CommandContainer<State extends ShellState<State>> {
    Map<String, Command<State>> getCommands();
}
