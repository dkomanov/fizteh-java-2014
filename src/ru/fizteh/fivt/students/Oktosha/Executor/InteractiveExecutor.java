package ru.fizteh.fivt.students.Oktosha.Executor;

import ru.fizteh.fivt.students.Oktosha.Command.Command;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtility;
import ru.fizteh.fivt.students.Oktosha.ConsoleUtility.ConsoleUtilityException;

import java.util.Scanner;

/**
 * InteractiveExecutor is a child of Executor
 * which runs ConsoleUtility in interactive mode
 */
public class InteractiveExecutor extends Executor {
    public static void execute(ConsoleUtility utility) {
        Scanner sc = new Scanner(System.in);
        for (; ; ) {
            System.out.print("$ ");
            String commandsString = sc.nextLine();
            try {
                Command[] commands = parse(commandsString);
                for (Command cmd : commands) {
                    utility.run(cmd);
                }
            } catch (ExecutorParseException e) {
                System.err.println("invalid syntax: empty command between two semicolons");
            } catch (ConsoleUtilityException e) {
                System.err.println(e.getMessage());
            }

        }
    }
}
