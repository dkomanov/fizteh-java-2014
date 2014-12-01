package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class PutCommand implements Commands {
    public void execute(String[] args, MyTable table) throws MyException {
        if (args.length > 3) {
            throw new MyException("put: too many arguments");
        }
        if (args.length < 3) {
            throw new MyException("put: not enough arguments");
        }

        String key = args[1];
        String value = args[2];

        if (table.currentTable.containsKey(key)) {
            System.out.println("overwrite\n" + table.currentTable.get(key));
        } else {
            System.out.println("new");
        }
        table.currentTable.put(key, value);
        table.changedKeys.add(key);
    }

    public String getName() {
        return "put";
    }
}
