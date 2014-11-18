package ru.fizteh.fivt.students.Volodin_Denis.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.storage.strings.TableProviderFactory;

public class JUnit {
    
    public static void main(final String[] args) {
        try {
            TableProviderFactory tpf = new TableProviderFactoryByVolodden();
            String dir = System.getProperty("fizteh.db.dir");
            TableProvider tableProvider = tpf.create(dir);
            Table table = tableProvider.createTable("1");

            Interpreter interpretator = new Interpreter();
            interpretator.run(args, tableProvider, table);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.exit(ReturnCodes.ERROR);
        }
        System.exit(ReturnCodes.SUCCESS);
    }
}
