package ru.fizteh.fivt.students.ryad0m.storeable;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class Main {

    public static void main(String[] args) {
        if (System.getProperty("fizteh.db.dir") != null) {
            TableProviderFactory tableProviderFactory = new MyTableProviderFactory();

            try {
                TableProvider tableProvider = tableProviderFactory.create(System.getProperty("fizteh.db.dir"));
                Shell shell = new Shell(tableProvider);
                shell.start(args);
            } catch (Exception e) {
                System.out.println("Something went wrong");
            }
        } else {
            System.out.println("Set fizteh.db.dir property!");
            System.exit(1);
        }
    }
}
