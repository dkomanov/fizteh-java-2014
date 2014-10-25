package ru.fiztech.fivt.students.theronsg.filemap;

public class FileMapMain {
    public static void main(final String[] args) {
        try {
            if (args.length == 0) {
                FileMapDistributor.interactiveMode();
            } else {
                FileMapDistributor.batchMode(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
