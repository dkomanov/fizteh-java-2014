package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.serverCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ServerLogic;

import java.util.List;

/**
 * @author AlexeyZhuravlev
 */
public class ListunersCommand extends ServerCommand {
    @Override
    public void execute(ServerLogic base) throws Exception {
        List<String> output = base.getListeners();
        for (String line: output) {
            System.out.println(line);
        }
    }
}
