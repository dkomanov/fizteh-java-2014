package ru.fizteh.fivt.students.ryad0m.junit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        if (System.getProperty("fizteh.db.dir") != null) {
            TableProviderFactory tableProviderFactory = new MyTableProviderFactory();

            try {
                TableProvider tableProvider = tableProviderFactory.create(System.getProperty("fizteh.db.dir"));
                Shell shell = new Shell(tableProvider);
                shell.start(args);
            } catch (IOException e) {
                System.out.println("IO error");
            } catch (BadFormatException e) {
                System.out.println("Format error");
            }
        } else {
            System.out.println("Obossan!");
            System.exit(1);
        }
    }
}
