package ru.fizteh.fivt.students.moskupols.cliutils2.interpreters;

import ru.fizteh.fivt.students.moskupols.cliutils.CommandExecutionException;
import ru.fizteh.fivt.students.moskupols.cliutils.StopProcessingException;
import ru.fizteh.fivt.students.moskupols.cliutils.UnknownCommandException;
import ru.fizteh.fivt.students.moskupols.cliutils2.CommandChooser;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.InvalidArgsException;

import java.util.Scanner;

/**
 * Created by moskupols on 02.12.14.
 */
public class InteractiveInterpreter extends Interpreter {
    private final Object prompt;
    private final Object context;
    private final CommandChooser chooser;

    public InteractiveInterpreter(Object prompt, Object context, CommandChooser chooser) {
        this.prompt = prompt;
        this.context = context;
        this.chooser = chooser;
    }

    @Override
    public void interpret() {
        Scanner scanner = new Scanner(System.in);
        boolean exited = false;
        do {
            System.err.flush();
            System.out.print(prompt);
            if (!scanner.hasNextLine()) {
                break;
            }

            for (String s : scanner.nextLine().split(";")) {
                try {
                    runJob(context, chooser, s);
                } catch (StopProcessingException e) {
                    exited = true;
                } catch (InvalidArgsException | UnknownCommandException | CommandExecutionException e) {
                    System.err.println(e.getMessage());
                }
            }
        } while (!exited);
    }
}
