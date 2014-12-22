package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.clientCommands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.ClientLogic;

/**
 * @author AlexeyZhuravlev
 */
public class WhereAmICommand extends ClientCommand {
    @Override
    public void execute(ClientLogic base) throws Exception {
        System.out.println(base.getPosition());
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
