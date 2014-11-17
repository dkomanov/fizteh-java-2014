package ru.fizteh.fivt.students.akhtyamovpavel.shell.commands;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */


public interface Command {
    void executeCommand(ArrayList<String> arguments) throws Exception;

    String getName();


}
