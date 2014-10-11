package ru.fiztech.fivt.students.theronsg.filemap;

public class FileMapMain {
    public static void main (final String[] args) {
        if (args.length == 0) {
            FileMapDistributor.interactiveMode();
        } else {
            FileMapDistributor.commandMode(args);
        }
    }
}
