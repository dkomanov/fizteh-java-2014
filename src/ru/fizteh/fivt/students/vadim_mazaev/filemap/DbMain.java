package ru.fizteh.fivt.students.vadim_mazaev.filemap;

public final class DbMain {
    private DbMain() {
        //not called
    }

    public static void main(final String[] args) throws Exception {
        try (DbConnector link = new DbConnector()) {
            if (args.length == 0) {
                CommandParser.interactiveMode(link);
            } else {
                CommandParser.packageMode(link, args);
            }
        } catch (ThrowExit t) {
            if (t.getExitStatus()) {
                System.exit(0);
            } else {
                System.exit(1);
            }
        }
    }
}
