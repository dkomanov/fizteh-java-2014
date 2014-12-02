package ru.fizteh.fivt.students.standy66_new.commands;

import java.util.Map;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public interface CommandFactory {
    Map<String, Command> getCommandMap(String locale);
}
