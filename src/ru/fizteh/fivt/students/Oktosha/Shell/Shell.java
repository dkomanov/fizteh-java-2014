package ru.fizteh.fivt.students.Oktosha.Shell;


import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Shell class implements an application,
 * which partially emulates UNIX command line.
 */
public final class Shell {
    /**
     * In this class params to run command are stored.
     */
    class Command {
        /**
         * Constructor
         *
         * @param cmdVal  will be assigned to cmd
         * @param argsVal will be assigned to args
         */
        public Command(final String cmdVal, final String[] argsVal) {
            cmd = cmdVal;
            args = argsVal;
        }

        /**
         * Cmd setter.
         *
         * @param str the value to set to cmd
         */
        public void setCmd(final String str) {
            cmd = str;
        }

        /**
         * Args setter.
         *
         * @param strArray the value to set to args
         */
        public void setArgs(final String[] strArray) {
            args = strArray;
        }

        /**
         * Cmd getter.
         *
         * @return actual value of cmd
         */
        public String getCmd() {
            return cmd;
        }

        /**
         * Args getter.
         *
         * @return actual value of args
         */
        public String[] getArgs() {
            return args;
        }

        /**
         * The name of command is stored in cmd.
         */
        private String cmd;
        /**
         * The arguments passed to command are stored in args.
         */
        private String[] args;
    }

    /**
     * @param args command line arguments are treated as shell commands
     *             if no arguments are given Shell runs in interactive mode
     */
    public static void main(final String[] args) {
        workingDirectory = Paths.get(".");
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    /**
     * The method which implements interactive mode of Shell.
     */
    private static void interactiveMode() {
        System.out.print("$");
    }

    /**
     * The method which implements package mode of Shell.
     *
     * @param args are command line arguments which are treated as commands
     */
    private static void packageMode(final String[] args) {
        System.out.println("package");
        String commandString = "";
        for (String s : args) {
            commandString = commandString.concat(" ");
            commandString = commandString.concat(s);
            System.out.println(s);
        }
        System.out.print(commandString);
    }

    /**
     * Command pwd prints current working directory.
     */
    private static void pwd() {
        System.out.println(workingDirectory.toAbsolutePath().normalize());
    }

    /**
     * a method which parses string with commands and returns array of Command.
     *
     * @param str contains commands which the user entered.
     * @return array of commands.
     */
   /* private Command[] parseCommandString(final String str) {
        String[] splitBySemicolon = str.split(";");
        Command[] ans = new Command[splitBySemicolon.length];
        for (String cmd : splitBySemicolon) {
            String[] cmdParsed = cmd.split("\\s");

        }
    }*/

    /**
     * A variable holding current working directory.
     */
    private static Path workingDirectory;

    /**
     * This is a private constructor, which is never called.
     */
    private Shell() {
    }
}

