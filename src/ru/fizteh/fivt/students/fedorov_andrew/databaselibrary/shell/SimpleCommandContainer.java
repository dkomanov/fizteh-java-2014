package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Log;
import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.support.Utility;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for classes that contain commands as fields.<br/>
 * Note that only fields of the last class in type inheritance tree are scanned.<br/>
 * Not accessible fields are not collected.
 * @see java.lang.reflect.Field#isAccessible()
 */
public class SimpleCommandContainer<State extends ShellState<State>> implements CommandContainer<State> {

    private Map<String, Command<State>> commandsMap;

    @Override
    public Map<String, Command<State>> getCommands() {
        if (commandsMap != null) {
            return commandsMap;
        }

        commandsMap = new HashMap<>();

        Class<?> containerClass = this.getClass();
        Field[] fields = containerClass.getFields();

        for (Field field : fields) {
            try {
                Command<State> command = (Command<State>) field.get(null);
                String commandName = Utility.simplifyFieldName(field.getName());
                commandsMap.put(commandName, command);
                Log.log("Registered command with name " + commandName);
            } catch (IllegalAccessException | ClassCastException exc) {
                Log.log(
                        SimpleCommandContainer.class, exc, "Failed to obtain Command field value: " + field);
            }
        }

        return commandsMap;
    }
}
