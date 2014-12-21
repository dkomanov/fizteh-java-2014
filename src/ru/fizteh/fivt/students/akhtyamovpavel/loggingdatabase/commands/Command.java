package ru.fizteh.fivt.students.akhtyamovpavel.loggingdatabase.commands;

import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */


public interface Command {
    String executeCommand(ArrayList<String> arguments) throws Exception;

    String getName();

}
