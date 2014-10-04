package ru.fizteh.fivt.students.ganiev.shell;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Move implements Command {
    public void invokeCommand(String[] arguments, Shell.MyShell shell) throws IOException {
        File source = FileCommander.getFile(arguments[0], shell);
        File destination = FileCommander.getFile(arguments[1], shell);

        if ((source.exists()) && (source.getPath().equals(destination.getPath()))) {
            return;
        }

        if ((source.exists()) && (source.isDirectory()) && (!destination.exists()) && (source.getParent().equals(destination.getParent()))) {
            if (!source.renameTo(destination)) {
                throw new IOException("Unable to move " + source.getPath() + " to " + destination.getPath());
            }
            return;
        }
        String[] argumentsWithMinusR = new String[3];
        argumentsWithMinusR[0] = "-r";
        argumentsWithMinusR[1] = arguments[0];
        argumentsWithMinusR[2] = arguments[1];
        (new Copy(3)).invokeCommand(argumentsWithMinusR, shell);
        (new Remove(2)).invokeCommand(Arrays.copyOfRange(argumentsWithMinusR, 0, 2), shell);
    }

    public int getNumberOfArguments() {
        return 2;
    }
}