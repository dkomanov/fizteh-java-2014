package ru.fizteh.fivt.students.vadim_mazaev.shell;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Shell {
    private Shell() {
        //not called
    }
    public static void commandMode(final String[] args) {
        List<String> cmdWithArgs = new ArrayList<String>();
        for (int i = 0; i < args.length; i++) {
            int separatorIndex = args[i].indexOf(";");
            if (separatorIndex != -1) {
                String part = args[i].substring(0, separatorIndex);
                if (!part.isEmpty()) {
                    cmdWithArgs.add(part);
                }
                ShellParser.parse(cmdWithArgs.
                        toArray(new String[cmdWithArgs.size()]), true);
                cmdWithArgs.clear();
                part = args[i].substring(separatorIndex + 1);
                if (!part.isEmpty()) {
                    cmdWithArgs.add(part);
                }
            } else {
                cmdWithArgs.add(args[i]);
            }
        }
        ShellParser.parse(cmdWithArgs.
                toArray(new String[cmdWithArgs.size()]), true);
    }
    public static void interactiveMode() {
        List<String> cmdWithArgs = new ArrayList<String>();
        StringBuilder builder = new StringBuilder();
        try (Scanner in = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                String line = "";
                try {
                     line = in.nextLine();
                } catch (NoSuchElementException e) {
                    System.exit(0);
                }
                String[] commands = line.split(";");
                for (String str : commands) {
                    char[] currentCmd = str.trim().toCharArray();
                    boolean quoteOpened = false;
                    for (int i = 0; i < currentCmd.length; i++) {
                        if (Character.isWhitespace(currentCmd[i])
                                && !quoteOpened) {
                            if (builder.length() != 0) {
                                cmdWithArgs.add(builder.toString());
                                builder.setLength(0);
                            }
                        } else if (currentCmd[i] == '\"') {
                            quoteOpened = !quoteOpened;
                        } else {
                            builder.append(currentCmd[i]);
                        }
                    }
                    if (quoteOpened) {
                        System.out
                        .println("Error parsing statement:"
                                + " odd number of quotes");
                    }
                    if (builder.length() != 0) {
                        cmdWithArgs.add(builder.toString());
                        builder.setLength(0);
                    }
                    ShellParser.parse(cmdWithArgs.
                            toArray(new String[cmdWithArgs.size()]), false);
                    cmdWithArgs.clear();
                }
            }
        }
    }
}
