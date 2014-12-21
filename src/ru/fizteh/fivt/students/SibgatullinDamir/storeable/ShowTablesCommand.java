package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

/**
 * Created by Lenovo on 20.10.2014.
 */
public class ShowTablesCommand implements CommandsForTables {
    public void execute(String[] args, MyTableProvider provider) throws MyException {
        if (args.length == 1) {
            throw new MyException("show tables: not enough arguments");
        }

        if (args.length == 2) {
            if (args[1].equals("tables")) {
                for (String key : provider.tables.keySet()) {
                    System.out.println(key + " " + provider.tables.get(key).size());
                }
            } else {
                throw new MyException("no such command");
            }
        } else {
            throw new MyException("show tables: too many arguments");
        }
    }

    public String getName() {
        return "show";
    }
}
