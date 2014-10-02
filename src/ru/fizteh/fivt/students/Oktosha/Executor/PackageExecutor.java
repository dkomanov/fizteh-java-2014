package ru.fizteh.fivt.students.Oktosha.Executor;

import ru.fizteh.fivt.students.Oktosha.Command.Command;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtility;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtilityRuntimeException;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtilitySyntaxException;

/**
 * Class which runs console utility in package mode
 */
public class PackageExecutor extends Executor {
    public static void execute(ConsoleUtility utility, String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String str : args) {
            builder.append(" ");
            builder.append(str);
        }
        String commandsString = builder.toString();
        try {
            Command[] commands = parse(commandsString);
            for (Command cmd : commands) {
                utility.run(cmd);
            }
        } catch (ExecutorParseException e) {
            System.err.println("invalid syntax: empty command between two semicolons");
            System.exit(Executor.SYNTAX_ERROR);
        } catch (ConsoleUtilitySyntaxException e) {
            System.err.println(e.getMessage());
            System.exit(Executor.SYNTAX_ERROR);
        } catch (ConsoleUtilityRuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(Executor.COMMAND_RUNTIME_ERROR);
        }
    }
}
