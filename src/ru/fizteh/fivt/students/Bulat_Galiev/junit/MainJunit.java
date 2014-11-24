package ru.fizteh.fivt.students.Bulat_Galiev.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;

public final class MainJunit {
    private MainJunit() {
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
