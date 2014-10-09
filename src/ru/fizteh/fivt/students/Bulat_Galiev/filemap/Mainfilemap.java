package ru.fizteh.fivt.students.Bulat_Galiev.filemap;

public final class Mainfilemap {
    private Mainfilemap() {
        // not called
    }

    public static void main(final String[] args) {
        try {
            Data link = new Data();
            if (args.length == 0) {
                Modesfilemap.interactiveMode(link);
            } else {
                Modesfilemap.packageMode(link, args);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
