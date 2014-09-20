package ru.fizteh.fivt.students.dsalnikov.shell;

import ru.fizteh.fivt.students.dsalnikov.shell.Commands.Command;
import ru.fizteh.fivt.students.dsalnikov.Utils.ShellState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.*;

public class Shell<State extends ShellState> {
    private ShellState state;

    //годный шелл
    public Shell() {
        state = new ShellState();
    }

    public Shell(State o) {
        state = o;
    }

    private Map<String, Command> CommandMap = new HashMap<String, Command>();

    public void setCommands(List<Command> cs) throws IllegalArgumentException {
        if (cs == null) {
            throw new IllegalArgumentException("this is madness");
        }
        for (Command Command : cs) {
            CommandMap.put(Command.getName(), Command);
        }
    }

    public void execute(String args[]) throws Exception {
        String concatenatedcmds = join(Arrays.asList(args), " ");
        String[] Commands = concatenatedcmds.split("\\s*;\\s*");
        for (String Command : Commands) {
            String[] cmdArgs = Command.split("\\s+");
            Command c = CommandMap.get(cmdArgs[0]);
            if (c == null) {
                throw new IllegalArgumentException("no such Command declared: " + cmdArgs[0]);
            }
            c.execute(cmdArgs);
        }
    }

    public static String join(Collection<?> objects, String separator) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object o : objects) {
            if (!first) {
                sb.append(separator);
            } else {
                first = false;
            }
            sb.append(o.toString());
        }
        return (sb.toString());
    }

    public void batchMode() throws Exception {
        boolean flag = true;
        Scanner sc = new Scanner(System.in);
        while (flag) {
            System.out.print("$ ");

            String cmd[] = new String[1];

            cmd[0] = sc.nextLine();
            if (cmd[0].isEmpty()) {
                continue;
            }
            try {
                this.execute(cmd);
            } catch (FileAlreadyExistsException fae) {
                System.err.println(fae.getMessage());
            } catch (FileNotFoundException fnf) {
                System.err.println(fnf.getMessage());
            } catch (IOException ioe) {
                System.err.println(ioe.getMessage());
            } catch (IllegalArgumentException iae) {
                System.err.println(iae.getMessage());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void commandMode(String[] args) throws Exception {
        try {
            this.execute(args);
        } catch (FileAlreadyExistsException fae) {
            System.err.println(fae.getMessage());
            System.exit(1);
        } catch (FileNotFoundException fnf) {
            System.err.println(fnf.getMessage());
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public ShellState getState() {
        return state;
    }
}


