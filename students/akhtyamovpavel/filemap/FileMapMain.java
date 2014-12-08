package ru.fizteh.fivt.students.akhtyamovpavel.filemap;

/**
 * Created by user1 on 30.09.2014.
 */
public class FileMapMain {
    public static void main(String[] args) {
        DataBaseShell shell = new DataBaseShell();

        if (args.length == 0) {
            shell.startInteractiveMode();
        } else {
            shell.startPacketMode(args);
        }
    }
}
