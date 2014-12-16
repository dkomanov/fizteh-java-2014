package ru.fizteh.fivt.students.elina_denisova.shell.commands;

import java.util.ArrayList;




public interface Command {
    void executeCommand(ArrayList<String> arguments) throws Exception;

    String getName();


}
