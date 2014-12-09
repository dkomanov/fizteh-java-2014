package ru.fizteh.fivt.students.dsalnikov.telnet;

import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StorableParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.ExitCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.Commands.ConnectCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.Commands.DisconnectCommand;
import ru.fizteh.fivt.students.dsalnikov.telnet.Commands.WhereAmICommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitriy on 12/8/2014.
 */
public class NewClientMain {
    public static void main(String[] args) {
        Shell sh = new Shell(new StorableParser());
        ClientState state = new ClientState();
        List<Command> commandList = new ArrayList<>();
        commandList.add(new ConnectCommand(state));
        commandList.add(new DisconnectCommand(state));
        commandList.add(new WhereAmICommand(state));
        commandList.add(new ExitCommand(sh));
        sh.setCommands(commandList);
        sh.run(args);
    }
}
