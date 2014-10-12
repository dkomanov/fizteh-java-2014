package ru.fizteh.fivt.students.kotsurba.filemap.shell;

public class CommandString {
    private String nativeCommand;
    private String[] argList;

    public CommandString(final String command) {
        nativeCommand = command;

        StringBuilder builder = new StringBuilder(command.trim());
        for (int i = 0; i < builder.length() - 1; ) {
            if ((builder.charAt(i) == ' ') && (builder.charAt(i + 1)) == ' ') {
                builder.deleteCharAt(i);
                continue;
            }
            ++i;
        }

        argList = builder.toString().split(" ");
    }

    public String getArg(final int index) {
        return argList[index];
    }

    public String getSpacedArg(final int index) {
        int currentIndex = 0;

        for (int i = 0; i < index; ++i) {
            currentIndex = nativeCommand.indexOf(argList[i], currentIndex) + argList[i].length();
        }

        return nativeCommand.substring(currentIndex + 1, nativeCommand.length());
    }

    public int length() {
        return argList.length;
    }
}
