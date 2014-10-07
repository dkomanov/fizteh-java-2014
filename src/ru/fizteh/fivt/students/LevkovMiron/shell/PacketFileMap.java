package ru.fizteh.fivt.students.LevkovMiron.shell;

/**
 * Created by Мирон on 22.09.2014 ru.fizteh.fivt.students.LevkovMiron.shell.
 */

class PacketFileMap extends FileMap {
    PacketFileMap() {
        super(System.err);
    }
    public void readCommands(final String[] args) {
        String argumentString = "";
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s + " ");
        }
        argumentString = builder.toString();
        argumentString = argumentString.trim();
        String[] commands = argumentString.split(";");
        for (String cmd : commands) {
            runCommand(cmd, System.err);
        }
        System.exit(1);
    }
}
