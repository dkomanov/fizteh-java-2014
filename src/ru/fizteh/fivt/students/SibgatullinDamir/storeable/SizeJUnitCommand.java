package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class SizeJUnitCommand implements Commands {
        public void execute(String[] args, MyTable table) throws MyException {
            if (args.length == 1) {
                if (table.currentTable != null) {
                    System.out.println(table.currentTable.size());
                } else {
                    throw new MyException("no table");
                }
            } else {
                throw new MyException("size: too many arguments");
            }
        }

        public String getName() {
            return "size";
        }
}
