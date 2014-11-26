package ru.fizteh.fivt.students.ekaterina_pogodina.shell;

import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public final class Shell {
    private Shell() {
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Bash mode
        List<String> cmdWithArgs = new ArrayList<String>();
        if (args.length != 0) {
            String line = "";
            for (int i = 0; i < args.length; i++) {
                line = line.concat(args[i]);
                if (i != args.length - 1) {
                    line = line.concat(" ");
                }
            }
            String[] commands = line.split(" ; ");
            for (int k = 0; k < commands.length; k++) {
                boolean flag = false;
                int index;
                int j;
                index = 0;
                j = 0;
                for (int i = 0; i < commands[k].length(); i++) {
                    if (commands[k].charAt(i) == ' ') {
                        cmdWithArgs.add(commands[k].substring(index, i));
                        j++;
                        index = i + 1;
                    }
                }
                cmdWithArgs.add(commands[k].substring(index, commands[k].length()));
                String[] arg = new String [cmdWithArgs.size()];
                for (int i = 0; i < arg.length; i++) {
                    arg[i] = cmdWithArgs.get(i);
                }
                if (j != 0 && arg[1].equals("-r")) {
                    flag = true;
                }
                try {
                    Parser.parse(arg, flag, true, j);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
                cmdWithArgs.clear();
            }
        } else {
            try (Scanner sc = new Scanner(System.in)) {
                while (true) {
                    System.out.print("$ ");
                    String line = "";
                    if (sc.hasNext()) {
                        line = sc.nextLine();
                    } else {
                        System.exit(0);
                    }
                    String[] commands = line.split(" ; ");
                    for (int k = 0; k < commands.length; k++) {
                        boolean flag = false;
                        int index;
                        int j;
                        index = 0;
                        j = 0;
                        for (int i = 0; i < commands[k].length(); i++) {
                            if (commands[k].charAt(i) == ' ') {
                                cmdWithArgs.add(commands[k].substring(index, i));
                                j++;
                                index = i + 1;
                            }
                        }
                        cmdWithArgs.add(commands[k].substring(index, commands[k].length()));
                        String[] arg = new String[cmdWithArgs.size()];
                        for (int i = 0; i < arg.length; i++) {
                            arg[i] = cmdWithArgs.get(i);
                        }
                        if (j != 0 && arg[1].equals("-r")) {
                            flag = true;
                        }
                        try {
                            Parser.parse(arg, flag, false, j);
                        } catch (Exception e) {
                            System.err.println(e.getMessage());
                        }
                        cmdWithArgs.clear();
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }

    }
}
