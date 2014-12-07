package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap;

/**
 * Created by user1 on 07.10.2014.
 */
public class MultiFileMapMain {
    public static void main(String[] args) {
        DataBaseShell shell = new DataBaseShell();
        if (args.length == 0) {
            shell.startInteractiveMode();
        } else {
            shell.startPacketMode(args);
        }
    }
}
