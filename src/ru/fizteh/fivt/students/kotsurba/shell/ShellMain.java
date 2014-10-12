package ru.fizteh.fivt.students.kotsurba.shell;

import ru.fizteh.fivt.students.kotsurba.filemap.commands.ShellExit;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.CommandParser;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.InvalidCommandException;
import ru.fizteh.fivt.students.kotsurba.filemap.shell.Shell;
import ru.fizteh.fivt.students.kotsurba.shell.Context.Context;
import ru.fizteh.fivt.students.kotsurba.shell.shellcommands.*;

import java.io.IOException;
import java.util.Scanner;

public class ShellMain {
    static final int END_OF_INPUT = -1;
    static final int END_OF_TRANSMISSION = 4;

    private static boolean isTerminativeSymbol(final int character) {
        return ((character == END_OF_INPUT) || (character == END_OF_TRANSMISSION));
    }

    public static boolean checkTerminate(final String str) {
        for (int i = 0; i < str.length(); ++i) {
            if (isTerminativeSymbol(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static void main(final String[] args) {
        try {
            Shell shell = new Shell();
            Context context = new Context();

            shell.addCommand(new ShellPwd(context));
            shell.addCommand(new ShellCd(context));
            shell.addCommand(new ShellMkdir(context));
            shell.addCommand(new ShellLs(context));
            shell.addCommand(new ShellRm(context));
            shell.addCommand(new ShellCp(context));
            shell.addCommand(new ShellMv(context));
            shell.addCommand(new ShellExit());
            shell.addCommand(new ShellCat(context));

            if (args.length > 0) {
                CommandParser parser = new CommandParser(args);
                while (!parser.isEmpty()) {
                    shell.executeCommand(parser.getCommand());
                }
            } else {
                Scanner scanner = new Scanner(System.in);
                System.out.print("$ ");
                while (true) {
                    try {
                        if (!scanner.hasNext()) {
                            System.exit(0);
                        }

                        String command = scanner.nextLine();

                        if (checkTerminate(command)) {
                            System.exit(0);
                        }

                        CommandParser parser = new CommandParser(command);
                        if (!parser.isEmpty()) {
                            shell.executeCommand(parser.getCommand());
                        }
                    } catch (InvalidCommandException e) {
                        System.err.println(e.getMessage());
                    } finally {
                        System.out.print("$ ");
                    }
                }

            }

        } catch (InvalidCommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't read current directory");
            System.exit(1);
        }
    }
}
