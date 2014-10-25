package ru.fiztech.fivt.students.theronsg.filemap;

public class FileMapMain {
    public static void main(final String[] args) {
<<<<<<< HEAD
        if (args.length == 0) {
            FileMapDistributor.interactiveMode();
        } else {
            FileMapDistributor.batchMode(args);
=======
        try {
            if (args.length == 0) {
                FileMapDistributor.interactiveMode();
            } else {
                FileMapDistributor.batchMode(args);
            }
        } catch (Exception e) {
            e.printStackTrace();
>>>>>>> 4ae82a8012874db357a99e99bf7eab7e401251ca
        }
    }
}
