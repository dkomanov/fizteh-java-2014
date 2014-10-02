package ru.fizteh.fivt.students.LevkovMiron.shell;

/**
 * Created by Мирон on 22.09.2014 ru.fizteh.fivt.students.LevkovMiron.shell.
 */
class PacketShell extends Shell {
    public void readCommands(final String[] args) {
        String argumentString = "";
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : args) {
            stringBuilder.append(s);
        }
        argumentString = stringBuilder.toString();
        argumentString.trim();
        String[] commands = argumentString.split(";");
        for (String cmd : commands) {
            runCommand(cmd, System.err);
        }
        exit();
    }
}
