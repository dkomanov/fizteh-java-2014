package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

import ru.fizteh.fivt.students.LebedevAleksey.Shell01.CommandParser;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParsedCommand;
import ru.fizteh.fivt.students.LebedevAleksey.Shell01.ParserException;

import java.util.List;

public class Interpreter extends CommandParser {

    private InterpreterState currentState;
    private List<Command> commandsList;

    public Interpreter(List<Command> commands, InterpreterState interpreterState) {
        commandsList = commands;
        currentState = interpreterState;
    }

    @Override
    protected boolean invokeCommands(List<ParsedCommand> commands) throws ParserException {
        for (ParsedCommand command : commands) {
            if (command.getCommandName() != null) {
                try {
                    boolean foundCommand = false;
                    for (Command cmd : commandsList) {
                        if (cmd.getName().equals(command.getCommandName())) {
                            foundCommand = true;
                            if (!cmd.invoke(currentState, command)) {
                                if (currentState.exited()) {
                                    return exit();
                                } else {
                                    return false;
                                }
                            }
                            break;
                        }
                    }
                    if (foundCommand) {
                        throw new ParserException("This command is unknown: " + command.getCommandName());
                    }
                } catch (ArgumentException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        return true;
    }
}
