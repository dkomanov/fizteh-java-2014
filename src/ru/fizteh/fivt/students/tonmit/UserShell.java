package ru.fizteh.fivt.students.tonmit.shell;

import java.util.Scanner;

public class UserShell {
    public boolean packetMode = false;

    public void printError(String errStr) {
        if (packetMode) {
            System.err.println(errStr);
            System.exit(1);
        } else {
            System.out.println(errStr);
        }
    }

    public boolean check(int num, String[] args) {
        if (args.length != num) {
            printError("Incorrect number of args");
            return false;
        }
        return true;
    }

    public String[] getArgsFromString(String str) {
        str = str.trim();
        str = str.replaceAll("[ ]+", " ");
        int countArgs = 1;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == ' ') {
                ++countArgs;
            }
        }
        if (!str.isEmpty()) {
            Scanner stringScanner = new Scanner(str);
            stringScanner.useDelimiter(" ");
            String[] cmdArgs = new String[countArgs];
            for (int i = 0; stringScanner.hasNext(); ++i) {
                cmdArgs[i] = stringScanner.next();
            }
            stringScanner.close();
            return cmdArgs;
        }
        return null;
    }

    public void execProc(String[] args)
    {}

    public void start(String[] args) {
        if (args.length != 0) {
            packetMode = true;
            StringBuffer argStr = new StringBuffer(args[0]);
            for (int i = 1; i < args.length; ++i) {
                argStr.append(" ");
                argStr.append(args[i]);
            }
            Scanner mainScanner = new Scanner(argStr.toString());
            mainScanner.useDelimiter("[ ]*;[ ]*");
            while (mainScanner.hasNext()) {
                String str = mainScanner.next();
                execProc(getArgsFromString(str));
            }
            mainScanner.close();
        } else {
        	packetMode = false;
            System.out.print("$ ");
            Scanner mainScanner = new Scanner(System.in);
            while (mainScanner.hasNextLine()) {
                String str = new String();
                str = mainScanner.nextLine();
                if (!str.isEmpty()) {
                    Scanner lineSeparator = new Scanner(str);
                    lineSeparator.useDelimiter("[ ]*;[ ]*");
                    while (lineSeparator.hasNext()) {
                        execProc(getArgsFromString(lineSeparator.next()));
                    }
                    lineSeparator.close();
                }
                System.out.print("$ ");
            }
            mainScanner.close();
            return;
        }
    }
}
