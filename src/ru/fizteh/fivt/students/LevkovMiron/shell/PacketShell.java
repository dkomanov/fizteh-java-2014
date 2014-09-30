package ru.fizteh.fivt.students.LevkovMiron.shell;

/**
 * Created by Мирон on 22.09.2014 ru.fizteh.fivt.students.LevkovMiron.shell.
 */
class PacketShell extends Shell {
    public void readCommands(final String[] args) {
        String argumentString = "";
        for (String s : args) {
            argumentString += s;
        }
        argumentString.trim();
        String[] commands = argumentString.split(";");
        for (String cmd : commands) {
            runCommand(cmd, System.err);
        }
        exit();
    }
}