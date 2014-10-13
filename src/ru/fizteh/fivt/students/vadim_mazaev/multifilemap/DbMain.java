package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

public final class DbMain {
    private DbMain() {
        //Not called, only for checkstyle.
    }

    public static void main(String[] args) {
        try {
            String dbDir = System.getProperty("fizteh.db.dir");
            TableManager manager = new DbConnector().create(dbDir);
            if (args.length == 0) {
                CommandParser.interactiveMode(manager);
            } else {
                CommandParser.batchMode(manager, args);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (ThrowExit t) {
            if (t.getExitStatus()) {
                System.exit(0);
            } else {
                System.exit(1);
            }
        }
    }
}
