package ru.fizteh.fivt.students.IvanShafran.shell;

public class ShellMain {

    public static void main(String[] args) {
        Shell shell = new Shell();

        if (args.length == 0) {
            shell.startInteractiveMode();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : args) {
                stringBuilder.append(string);
                stringBuilder.append(" ");
            }

            shell.startPacketMode(stringBuilder.toString());
        }
    }
}
