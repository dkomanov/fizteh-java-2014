package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

/**
 * Created by Lenovo on 01.10.2014.
 */
public class RemoveCommand implements Commands {
    public void execute(String[] args, MyTable table) throws MyException {
        if (args.length < 2) {
            throw new MyException("remove: not enough arguments");
        }
        if (args.length > 3) {
            throw new MyException("remove: too many arguments");
        }

        String key = args[1];

        if (table.currentTable.containsKey(key)) {
            table.currentTable.remove(key);
            table.changedKeys.add(key);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

    public String getName() {
        return "remove";
    }
}
