package ru.fizteh.fivt.students.dsalnikov.telnet;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StorableParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.telnet.Commands.ListUsersCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.Commands.StartCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.Commands.StopCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitriy on 12/8/2014.
 */
public class NewServerMain {

    public static void main(String[] args) {
        Shell sh = new Shell(new StorableParser());
        List<Command> commandList = new ArrayList<>();
        ServerState state = new ServerState();
        commandList.add(new StartCommand(state));
        commandList.add(new StopCommand(state));
        commandList.add(new ListUsersCommand(state));
        sh.setCommands(commandList);
        sh.run(args);
    }
}
