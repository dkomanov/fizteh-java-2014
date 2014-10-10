package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

import ru.fizteh.fivt.students.vadim_mazaev.filemap.ThrowExit;

public final class DbMain {
    private DbMain() {
        //not called
    }

    public static void main(final String[] args) throws Exception {
        try {
            String dbDir = System.getProperty("fizteh.db.dir");
            TableManager manager = new DbConnector().create(dbDir);
            if (args.length == 0) {
                CommandParser.interactiveMode(manager);
            } else {
                CommandParser.packageMode(manager, args);
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
