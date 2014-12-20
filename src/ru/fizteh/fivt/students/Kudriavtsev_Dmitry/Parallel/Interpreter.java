package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Created by ВАНЯ on 19.12.2014.
 */
public class Interpreter {
    Welcome mainClass;
    private final InputStream in;
    private final PrintStream out;
    private final PrintStream err;

    public Interpreter(Welcome mainClass, InputStream in, PrintStream out, PrintStream err) {
        if (in == null || out == null || err == null) {
            throw new IllegalArgumentException("InputStream or OutputStream is null");
        }
        this.mainClass = mainClass;
        this.in = in;
        this.out = out;
        this.err = err;
    }

    private boolean exitWarning() {
        out.println("There are uncommited changes! Do you want to exit without commit?(y/n)");
        Scanner sc = new Scanner(in);
        String answer = sc.nextLine();
        while (!answer.equals("y") && !answer.equals("n")) {
            out.println("Bad answer, please write 'y' or 'n' without brackets");
            answer = sc.nextLine();
        }
        return answer.equals("y");
    }

    public void batchMode(String[] args) throws IllegalStateException {
        String merged = args[0];
        String[] s;
        for (int i = 1; i < args.length; ++i) {
            merged += " ";
            merged += args[i];
        }
        s = merged.split(";");
        for (String newCommand : s) {
            int j = 0;
            String[] arguments = newCommand.split("\\s+");
            while (j < arguments.length && arguments[j].equals("")) {
                ++j;
            }
            if (j >= arguments.length) {
                continue;
            }
            if (arguments[j].equals("exit")) {
                if (!mainClass.getActiveTable().getNewKey().isEmpty()
                        || !mainClass.getActiveTable().getRemoved().isEmpty()) {
                    if (exitWarning()) {
                        break;
                    }
                } else {
                    break;
                }
            }
            Command whatToDo = mainClass.commands.get(arguments[j]);
            if (whatToDo == null) {
                out.println("Not found command: " + arguments[j]);
                throw new IllegalStateException("Not found command: " + arguments[j]);
            }
            String[] newArgs = new String[arguments.length - j - 1];
            System.arraycopy(arguments, j + 1, newArgs, 0, newArgs.length);
            run(whatToDo.name, newArgs, true, false);
        }
    }

    public void interactiveMode() {
        String[] arguments;
        Scanner sc = new Scanner(in);
        String s1;
        boolean exit = false;
        while (true) {
            try {
                if (sc.hasNext()) {
                    out.print("$ ");
                }
                s1 = sc.nextLine();
            } catch (Exception e) {
                return;
            }
            String[] s = s1.split(";");
            for (String newCommand : s) {
                while (newCommand.startsWith(" ")) {
                    newCommand = newCommand.substring(1, newCommand.length());
                }
                arguments = newCommand.split("\\s+");
                if (arguments[0].equals("exit")) {
                    exit = true;
                    break;
                }
                if (arguments[0].equals("")) {
                    break;
                }
                Command whatToDo = mainClass.commands.get(arguments[0]);
                if (whatToDo == null) {
                    err.println("Not found command: " + arguments[0]);
                    if (s.length > 1) {
                        break;
                    }
                    continue;
                }
                String[] newArgs = new String[arguments.length - 1];
                if (arguments.length != 0) {
                    System.arraycopy(arguments, 1, newArgs, 0, newArgs.length);
                }
                if (s.length > 1) {
                    if (!run(whatToDo.name, newArgs, false, true)) {
                        break;
                    }
                } else {
                    run(whatToDo.name, newArgs, false, false);
                }
            }
            if (exit) {
                if (mainClass.getActiveTable() == null) {
                    break;
                }
                if (!mainClass.getActiveTable().getNewKey().isEmpty()
                        || !mainClass.getActiveTable().getRemoved().isEmpty()) {
                    if (exitWarning()) {
                        break;
                    }
                    exit = false;
                } else {
                    break;
                }
            }
        }
    }

    public boolean run(String name, String[] args, boolean batchMode, boolean batchModeInInteractive) {
        Command command = mainClass.commands.get(name);
        command.batchMode = batchMode;
        command.batchModeInInteractive = batchModeInInteractive;
        if (command != null) {
            if (!command.exec(mainClass, args, out, err)) {
                return false;
            }
        } else if (!args[0].equals("")) {
            err.println(args[0] + " : command not found");
            return false;
        }
        return true;
    }
}
