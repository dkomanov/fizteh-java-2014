package ru.fizteh.fivt.students.Bulat_Galiev.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;

public final class MainStoreable {
    private MainStoreable() {
        // Disable instantiation to this class.
    }

    public static void main(final String[] args) {
        try {
            String databaseDir = System.getProperty("fizteh.db.dir");
            if (databaseDir == null) {
                System.err.println("specify the path to fizteh.db.dir");
                System.exit(-1);
            }
            TableProvider provider = new TabledbProviderFactory()
                    .create(databaseDir);
            new Interpreter(provider, args);
        } catch (Exception e) {
            System.err.print(e.getMessage());
            System.exit(-1);
        }
    }
}
